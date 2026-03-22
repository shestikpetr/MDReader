package markdown.inline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.*

private val codeBackground = Color(0x18000000)
private val linkColor      = Color(0xFF1565C0)

fun buildInlineContent(startNode: Node?): AnnotatedString = buildAnnotatedString {
    appendInline(startNode)
}

private fun AnnotatedString.Builder.appendInline(startNode: Node?) {
    var node = startNode
    while (node != null) {
        when (node) {
            is Text -> append(node.literal)

            is StrongEmphasis -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                appendInline(node.firstChild)
            }

            is Emphasis -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                appendInline(node.firstChild)
            }

            is Strikethrough -> withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                appendInline(node.firstChild)
            }

            is Code -> withStyle(
                SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    background = codeBackground
                )
            ) {
                append(node.literal)
            }

            is Link -> withStyle(
                SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                appendInline(node.firstChild)
            }

            is Image -> withStyle(SpanStyle(color = Color.Gray, fontStyle = FontStyle.Italic)) {
                append("[${node.title ?: node.destination}]")
            }

            is SoftLineBreak -> append(" ")
            is HardLineBreak -> append("\n")
        }
        node = node.next
    }
}
