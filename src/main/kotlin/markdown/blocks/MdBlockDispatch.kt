package markdown.blocks

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.*

@Composable
fun MdBlockNodes(startNode: Node?) {
    var node = startNode
    while (node != null) {
        MdBlock(node)
        node = node.next
    }
}

@Composable
internal fun MdBlock(node: Node) {
    when (node) {
        is Heading           -> MdHeading(node)
        is Paragraph         -> MdParagraph(node)
        is FencedCodeBlock   -> MdCodeBlock(node.literal.trimEnd(), node.info.trim().substringBefore(' '))
        is IndentedCodeBlock -> MdCodeBlock(node.literal.trimEnd())
        is BlockQuote        -> MdBlockQuote(node)
        is BulletList        -> MdBulletList(node)
        is OrderedList       -> MdOrderedList(node)
        is ThematicBreak     -> HorizontalDivider()
        is TableBlock        -> MdTable(node)
        else                 -> Unit
    }
}
