package markdown

import org.commonmark.node.Code
import org.commonmark.node.Document
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.Image
import org.commonmark.node.Node
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.Text as MdText

data class HeadingEntry(val level: Int, val text: String, val itemIndex: Int)

fun extractHeadings(document: Document): List<HeadingEntry> {
    val result = mutableListOf<HeadingEntry>()
    var node = document.firstChild
    var index = 0
    while (node != null) {
        if (node is Heading) {
            val text = collectInlineText(node).trim()
            if (text.isNotEmpty()) {
                result.add(HeadingEntry(node.level, text, index))
            }
        }
        index++
        node = node.next
    }
    return result
}

private fun collectInlineText(parent: Node): String = buildString {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is MdText -> append(child.literal)
            is Code -> append(child.literal)
            is SoftLineBreak, is HardLineBreak -> append(' ')
            is Image -> append(collectInlineText(child))
            else -> append(collectInlineText(child))
        }
        child = child.next
    }
}
