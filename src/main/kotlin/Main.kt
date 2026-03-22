import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import file.openFile
import markdown.HeadingEntry
import markdown.MarkdownViewer
import markdown.extractHeadings
import markdown.mdParser
import org.commonmark.node.Document
import ui.AppToolbar
import ui.ResizableLayout
import ui.StatusBar
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

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MD Reader",
        state = rememberWindowState(width = 1000.dp, height = 750.dp)
    ) {
        var markdownText by remember { mutableStateOf<String?>(null) }
        var windowTitle  by remember { mutableStateOf("MD Reader") }
        var synced       by remember { mutableStateOf(true) }
        var textScale    by remember { mutableStateOf(1f) }
        var isDarkTheme  by remember { mutableStateOf(false) }
        var currentFile  by remember { mutableStateOf<File?>(null) }
        var headings     by remember { mutableStateOf(emptyList<HeadingEntry>()) }
        val lazyListState = rememberLazyListState()
        val focusRequester = remember { FocusRequester() }

        fun zoomIn()    { textScale = ((textScale * 10).roundToInt() + 1).coerceAtMost(30) / 10f }
        fun zoomOut()   { textScale = ((textScale * 10).roundToInt() - 1).coerceAtLeast(5)  / 10f }
        fun zoomReset() { textScale = 1f }

        fun loadFile(file: File) {
            val text = file.readText()
            markdownText = text
            windowTitle = file.name
            currentFile = file
            headings = extractHeadings(mdParser.parse(text) as Document)
        }

        SideEffect { window.title = windowTitle }

        LaunchedEffect(currentFile) {
            currentFile?.let { file ->
                var lastModified = file.lastModified()
                while (isActive) {
                    delay(1000)
                    val newModified = withContext(Dispatchers.IO) { file.lastModified() }
                    if (newModified != lastModified) {
                        lastModified = newModified
                        val newText = withContext(Dispatchers.IO) { file.readText() }
                        markdownText = newText
                        headings = extractHeadings(mdParser.parse(newText) as Document)
                    }
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
                        files.firstOrNull()?.let { loadFile(it) }
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

        LaunchedEffect(Unit) { focusRequester.requestFocus() }

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
                            when {
                                event.isCtrlPressed && event.key == Key.O -> {
                                    openFile()?.let { (file, _) -> loadFile(file) }
                                    true
                                }
                                event.isCtrlPressed && event.key == Key.Equals -> { zoomIn(); true }
                                event.isCtrlPressed && event.key == Key.Minus  -> { zoomOut(); true }
                                event.isCtrlPressed && event.key == Key.Zero   -> { zoomReset(); true }
                                else -> false
                            }
                        }
                ) {
                    AppToolbar(
                        title = windowTitle,
                        synced = synced,
                        textScale = textScale,
                        isDarkTheme = isDarkTheme,
                        onSyncedChange = { synced = it },
                        onOpenFile = { openFile()?.let { (file, _) -> loadFile(file) } },
                        onZoomIn = ::zoomIn,
                        onZoomOut = ::zoomOut,
                        onZoomReset = ::zoomReset,
                        onDarkThemeChange = { isDarkTheme = it }
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        ResizableLayout(
                            synced = synced,
                            headings = headings,
                            contentListState = lazyListState
                        ) {
                            if (markdownText != null) {
                                MarkdownViewer(
                                    content = markdownText!!,
                                    textScale = textScale,
                                    lazyListState = lazyListState
                                )
                            } else {
                                WelcomeScreen()
                            }
                        }
                    }
                    if (markdownText != null) {
                        val words = markdownText!!.trim().split(Regex("\\s+")).count { it.isNotEmpty() }
                        StatusBar(wordCount = words, charCount = markdownText!!.length)
                    }
                }
            }
        }
    }
}
