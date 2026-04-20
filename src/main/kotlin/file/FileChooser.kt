package file

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter

private val mdFilter = FilenameFilter { _, name ->
    val lower = name.lowercase()
    lower.endsWith(".md") || lower.endsWith(".markdown") || lower.endsWith(".txt")
}

fun openFile(parent: Frame? = null, initialDir: File? = null): Pair<File, String>? {
    val dialog = FileDialog(parent, "Open Markdown File", FileDialog.LOAD).apply {
        filenameFilter = mdFilter
        if (initialDir != null && initialDir.isDirectory) {
            directory = initialDir.absolutePath
        }
        file = "*.md;*.markdown;*.txt"
        isMultipleMode = false
        isVisible = true
    }
    val dir = dialog.directory ?: return null
    val name = dialog.file ?: return null
    val file = File(dir, name)
    if (!file.isFile) return null
    return file to file.readText()
}
