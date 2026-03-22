package markdown.blocks

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import markdown.LocalTextScale
import markdown.inline.buildInlineContent
import org.commonmark.node.Paragraph

@Composable
internal fun MdParagraph(node: Paragraph) {
    val scale = LocalTextScale.current
    Text(
        text = buildInlineContent(node.firstChild),
        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = (28 * scale).sp),
        color = MaterialTheme.colorScheme.onBackground
    )
}
