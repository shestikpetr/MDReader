package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import markdown.HeadingEntry

@Composable
fun SidePanel(
    width: Dp,
    headings: List<HeadingEntry>,
    contentListState: LazyListState
) {
    val scope = rememberCoroutineScope()
    val tocListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        if (headings.isNotEmpty()) {
            LazyColumn(
                state = tocListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(headings) { heading ->
                    TocItem(
                        heading = heading,
                        onClick = {
                            scope.launch {
                                contentListState.animateScrollToItem(heading.itemIndex)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TocItem(heading: HeadingEntry, onClick: () -> Unit) {
    val indent = ((heading.level - 1) * 10).dp
    val style = when (heading.level) {
        1 -> MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
        2 -> MaterialTheme.typography.bodySmall
        else -> MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
    }
    Text(
        text = heading.text,
        style = style,
        color = if (heading.level == 1)
            MaterialTheme.colorScheme.onSurface
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = 12.dp + indent,
                end = 8.dp,
                top = 4.dp,
                bottom = 4.dp
            )
    )
}
