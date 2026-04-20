package settings

import java.io.File
import java.util.Properties

object AppSettings {
    private val configDir: File = run {
        val appData = System.getenv("APPDATA")
        if (!appData.isNullOrBlank()) File(appData, "MDReader")
        else File(System.getProperty("user.home"), ".config/MDReader")
    }
    private val file: File = File(configDir, "settings.properties")
    private val props = Properties()
    private var loaded = false

    fun load() {
        if (loaded) return
        loaded = true
        if (file.isFile) runCatching { file.inputStream().use(props::load) }
    }

    fun save() {
        runCatching {
            configDir.mkdirs()
            file.outputStream().use { props.store(it, "MDReader settings") }
        }
    }

    private fun getInt(key: String, default: Int): Int =
        props.getProperty(key)?.toIntOrNull() ?: default
    private fun getFloat(key: String, default: Float): Float =
        props.getProperty(key)?.toFloatOrNull() ?: default
    private fun getBool(key: String, default: Boolean): Boolean =
        props.getProperty(key)?.toBooleanStrictOrNull() ?: default
    private fun getStr(key: String, default: String? = null): String? =
        props.getProperty(key) ?: default

    var windowWidth: Int
        get() = getInt("window.width", 1000)
        set(v) { props.setProperty("window.width", v.toString()) }
    var windowHeight: Int
        get() = getInt("window.height", 750)
        set(v) { props.setProperty("window.height", v.toString()) }
    var windowX: Int
        get() = getInt("window.x", Int.MIN_VALUE)
        set(v) { props.setProperty("window.x", v.toString()) }
    var windowY: Int
        get() = getInt("window.y", Int.MIN_VALUE)
        set(v) { props.setProperty("window.y", v.toString()) }
    var windowMaximized: Boolean
        get() = getBool("window.maximized", false)
        set(v) { props.setProperty("window.maximized", v.toString()) }

    var isDarkTheme: Boolean
        get() = getBool("theme.dark", false)
        set(v) { props.setProperty("theme.dark", v.toString()) }

    var textScale: Float
        get() = getFloat("text.scale", 1f)
        set(v) { props.setProperty("text.scale", v.toString()) }

    var synced: Boolean
        get() = getBool("panel.synced", true)
        set(v) { props.setProperty("panel.synced", v.toString()) }

    var leftPanelWidth: Int
        get() = getInt("panel.left.width", 150)
        set(v) { props.setProperty("panel.left.width", v.toString()) }
    var rightPanelWidth: Int
        get() = getInt("panel.right.width", 150)
        set(v) { props.setProperty("panel.right.width", v.toString()) }

    var lastDirectory: String?
        get() = getStr("file.lastDirectory")
        set(v) {
            if (v == null) props.remove("file.lastDirectory")
            else props.setProperty("file.lastDirectory", v)
        }

    var recentFiles: List<String>
        get() = getStr("file.recent")
            ?.split('\n')
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        set(v) {
            val trimmed = v.take(10)
            props.setProperty("file.recent", trimmed.joinToString("\n"))
        }

    fun pushRecentFile(path: String) {
        val current = recentFiles.filter { it != path }
        recentFiles = listOf(path) + current
        save()
    }
}
