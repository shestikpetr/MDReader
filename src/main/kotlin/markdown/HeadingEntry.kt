package markdown

import org.commonmark.node.Document
import org.commonmark.node.Heading
import org.commonmark.node.Text as MdText

data class HeadingEntry(val level: Int, val text: String, val itemIndex: Int)

fun extractHeadings(document: Document): List<HeadingEntry> {
    val result = mutableListOf<HeadingEntry>()
    var node = document.firstChild
    var index = 0
    while (node != null) {
        if (node is Heading) {
            val text = buildString {
                var inline = node.firstChild
                while (inline != null) {
                    if (inline is MdText) append(inline.literal)
                    inline = inline.next
                }
            }
            result.add(HeadingEntry(node.level, text, index))
        }
        index++
        node = node.next
    }
    return result
}
