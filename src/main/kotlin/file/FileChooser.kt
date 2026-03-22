package file

import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter

fun openFile(): Pair<File, String>? {
    runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }
    val chooser = JFileChooser().apply {
        fileFilter = FileNameExtensionFilter("Markdown files (*.md, *.markdown)", "md", "markdown", "txt")
        dialogTitle = "Open Markdown File"
    }
    return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile.let { it to it.readText() }
    } else null
}
