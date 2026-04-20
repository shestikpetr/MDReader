import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import file.openFile
import markdown.MarkdownViewer
import markdown.extractHeadings
import markdown.mdParser
import org.commonmark.node.Document
import settings.AppSettings
import tabs.Tab
import ui.AppToolbar
import ui.ResizableLayout
import ui.StatusBar
import ui.TabBar
import ui.WelcomeScreen
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.awt.event.MouseWheelListener
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

fun main(args: Array<String>) {
    AppSettings.load()
    application {
        val initialFile = args.firstOrNull()?.let { File(it) }?.takeIf { it.exists() && it.extension == "md" }
        val windowState = rememberWindowState(
            width = AppSettings.windowWidth.dp,
            height = AppSettings.windowHeight.dp,
            placement = if (AppSettings.windowMaximized) WindowPlacement.Maximized else WindowPlacement.Floating
        )
        Window(
            onCloseRequest = {
                AppSettings.windowWidth = windowState.size.width.value.toInt()
                AppSettings.windowHeight = windowState.size.height.value.toInt()
                AppSettings.windowMaximized = windowState.placement == WindowPlacement.Maximized
                AppSettings.save()
                exitApplication()
            },
            title = "MD Reader",
            icon = painterResource("icon.svg"),
            state = windowState
        ) {
            val tabs = remember { mutableStateListOf<Tab>() }
            var activeIndex by remember { mutableStateOf(-1) }
            var synced       by remember { mutableStateOf(AppSettings.synced) }
            var textScale    by remember { mutableStateOf(AppSettings.textScale) }
            var isDarkTheme  by remember { mutableStateOf(AppSettings.isDarkTheme) }
            val focusRequester = remember { FocusRequester() }

            val activeTab = tabs.getOrNull(activeIndex)

            fun zoomIn()    { textScale = ((textScale * 10).roundToInt() + 1).coerceAtMost(30) / 10f }
            fun zoomOut()   { textScale = ((textScale * 10).roundToInt() - 1).coerceAtLeast(5)  / 10f }
            fun zoomReset() { textScale = 1f }

            fun loadFile(file: File) {
                val text = runCatching { file.readText() }.getOrNull() ?: return
                val h = extractHeadings(mdParser.parse(text) as Document)
                val existing = tabs.indexOfFirst { it.file.absolutePath == file.absolutePath }
                if (existing >= 0) {
                    tabs[existing].content = text
                    tabs[existing].headings = h
                    activeIndex = existing
                } else {
                    tabs.add(Tab(file, text, h))
                    activeIndex = tabs.lastIndex
                }
                file.parentFile?.absolutePath?.let { AppSettings.lastDirectory = it }
                AppSettings.pushRecentFile(file.absolutePath)
            }

            fun closeTab(index: Int) {
                if (index !in tabs.indices) return
                tabs.removeAt(index)
                activeIndex = when {
                    tabs.isEmpty() -> -1
                    index < activeIndex -> activeIndex - 1
                    activeIndex >= tabs.size -> tabs.lastIndex
                    else -> activeIndex
                }
            }

            fun nextTab() {
                if (tabs.isEmpty()) return
                activeIndex = (activeIndex + 1).mod(tabs.size)
            }
            fun prevTab() {
                if (tabs.isEmpty()) return
                activeIndex = (activeIndex - 1).mod(tabs.size)
            }

            SideEffect {
                window.title = activeTab?.let { "${it.title} — MD Reader" } ?: "MD Reader"
            }

            LaunchedEffect(synced)      { AppSettings.synced      = synced;      AppSettings.save() }
            LaunchedEffect(textScale)   { AppSettings.textScale   = textScale;   AppSettings.save() }
            LaunchedEffect(isDarkTheme) { AppSettings.isDarkTheme = isDarkTheme; AppSettings.save() }

            LaunchedEffect(activeTab?.id) {
                val tab = activeTab ?: return@LaunchedEffect
                var lastModified = tab.file.lastModified()
                while (isActive) {
                    delay(1000)
                    val newModified = withContext(Dispatchers.IO) { tab.file.lastModified() }
                    if (newModified != lastModified) {
                        lastModified = newModified
                        val newText = withContext(Dispatchers.IO) { tab.file.readText() }
                        tab.content = newText
                        tab.headings = extractHeadings(mdParser.parse(newText) as Document)
                    }
                }
            }

            DisposableEffect(Unit) {
                val dropTarget = object : DropTarget() {
                    @Suppress("UNCHECKED_CAST")
                    override fun drop(event: DropTargetDropEvent) {
                        event.acceptDrop(DnDConstants.ACTION_COPY)
                        runCatching {
                            val files = event.transferable
                                .getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                            files.forEach { loadFile(it) }
                        }
                        event.dropComplete(true)
                    }
                }
                window.contentPane.dropTarget = dropTarget

                val wheelListener = MouseWheelListener { e ->
                    if (e.isControlDown) {
                        if (e.wheelRotation > 0) zoomOut() else zoomIn()
                    }
                }
                window.addMouseWheelListener(wheelListener)

                onDispose {
                    window.contentPane.dropTarget = null
                    window.removeMouseWheelListener(wheelListener)
                }
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                val fileToLoad = initialFile
                    ?: AppSettings.recentFiles.firstOrNull()?.let(::File)?.takeIf { it.isFile }
                fileToLoad?.let { loadFile(it) }
            }

            val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
            MaterialTheme(colorScheme = colorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester)
                            .focusable()
                            .onKeyEvent { event ->
                                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                                val initialDir = AppSettings.lastDirectory?.let(::File)
                                when {
                                    event.isCtrlPressed && event.key == Key.O -> {
                                        openFile(window, initialDir)?.let { (file, _) -> loadFile(file) }
                                        true
                                    }
                                    event.isCtrlPressed && event.key == Key.W -> {
                                        if (activeIndex >= 0) closeTab(activeIndex); true
                                    }
                                    event.isCtrlPressed && event.key == Key.Tab -> {
                                        if (event.isShiftPressed) prevTab() else nextTab(); true
                                    }
                                    event.isCtrlPressed && event.key == Key.Equals -> { zoomIn(); true }
                                    event.isCtrlPressed && event.key == Key.Minus  -> { zoomOut(); true }
                                    event.isCtrlPressed && event.key == Key.Zero   -> { zoomReset(); true }
                                    else -> false
                                }
                            }
                    ) {
                        AppToolbar(
                            synced = synced,
                            textScale = textScale,
                            isDarkTheme = isDarkTheme,
                            onSyncedChange = { synced = it },
                            onOpenFile = {
                                val initialDir = AppSettings.lastDirectory?.let(::File)
                                openFile(window, initialDir)?.let { (file, _) -> loadFile(file) }
                            },
                            onZoomIn = ::zoomIn,
                            onZoomOut = ::zoomOut,
                            onZoomReset = ::zoomReset,
                            onDarkThemeChange = { isDarkTheme = it }
                        )
                        if (tabs.isNotEmpty()) {
                            TabBar(
                                tabs = tabs,
                                activeIndex = activeIndex,
                                onActivate = { activeIndex = it },
                                onClose = { closeTab(it) },
                                onOpenNew = {
                                    val initialDir = AppSettings.lastDirectory?.let(::File)
                                    openFile(window, initialDir)?.let { (file, _) -> loadFile(file) }
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            val tab = activeTab
                            val fallbackListState = rememberLazyListState()
                            ResizableLayout(
                                synced = synced,
                                headings = tab?.headings.orEmpty(),
                                contentListState = tab?.lazyListState ?: fallbackListState
                            ) {
                                if (tab != null) {
                                    MarkdownViewer(
                                        content = tab.content,
                                        textScale = textScale,
                                        lazyListState = tab.lazyListState
                                    )
                                } else {
                                    WelcomeScreen()
                                }
                            }
                        }
                        activeTab?.let { tab ->
                            val words = tab.content.trim().split(Regex("\\s+")).count { it.isNotEmpty() }
                            StatusBar(wordCount = words, charCount = tab.content.length)
                        }
                    }
                }
            }
        }
    }
}
