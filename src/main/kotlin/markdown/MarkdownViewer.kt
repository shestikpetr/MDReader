package markdown

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import markdown.blocks.MdBlock
import org.commonmark.node.Document
import org.commonmark.node.Node

@Composable
fun MarkdownViewer(
    content: String,
    textScale: Float = 1f,
    lazyListState: LazyListState = rememberLazyListState()
) {
    val document = remember(content) { mdParser.parse(content) as Document }
    val topLevelNodes = remember(document) {
        buildList<Node> {
            var node = document.firstChild
            while (node != null) { add(node); node = node.next }
        }
    }

    val base = MaterialTheme.typography
    val scaledTypography = remember(textScale) {
        base.copy(
            headlineLarge  = base.headlineLarge.copy(fontSize  = base.headlineLarge.fontSize  * textScale),
            headlineMedium = base.headlineMedium.copy(fontSize = base.headlineMedium.fontSize * textScale),
            headlineSmall  = base.headlineSmall.copy(fontSize  = base.headlineSmall.fontSize  * textScale),
            titleLarge     = base.titleLarge.copy(fontSize     = base.titleLarge.fontSize     * textScale),
            titleMedium    = base.titleMedium.copy(fontSize    = base.titleMedium.fontSize    * textScale),
            titleSmall     = base.titleSmall.copy(fontSize     = base.titleSmall.fontSize     * textScale),
            bodyLarge      = base.bodyLarge.copy(fontSize      = base.bodyLarge.fontSize      * textScale),
            bodyMedium     = base.bodyMedium.copy(fontSize     = base.bodyMedium.fontSize     * textScale),
            bodySmall      = base.bodySmall.copy(fontSize      = base.bodySmall.fontSize      * textScale),
            labelLarge     = base.labelLarge.copy(fontSize     = base.labelLarge.fontSize     * textScale),
            labelMedium    = base.labelMedium.copy(fontSize    = base.labelMedium.fontSize    * textScale),
            labelSmall     = base.labelSmall.copy(fontSize     = base.labelSmall.fontSize     * textScale),
        )
    }

    MaterialTheme(typography = scaledTypography) {
        CompositionLocalProvider(LocalTextScale provides textScale) {
            Box(modifier = Modifier.fillMaxSize()) {
                SelectionContainer {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(topLevelNodes.size) { index ->
                            MdBlock(topLevelNodes[index])
                        }
                    }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(lazyListState),
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                )
            }
        }
    }
}
