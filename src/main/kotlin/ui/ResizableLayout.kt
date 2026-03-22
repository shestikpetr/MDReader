package ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import markdown.HeadingEntry

private val PANEL_MIN = 0.dp
private val PANEL_MAX = 600.dp
private val PANEL_DEFAULT = 150.dp

@Composable
fun ResizableLayout(
    synced: Boolean,
    headings: List<HeadingEntry>,
    contentListState: LazyListState,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    var leftWidth by remember { mutableStateOf(PANEL_DEFAULT) }
    var rightWidth by remember { mutableStateOf(PANEL_DEFAULT) }

    // When sync is turned on — align right to left
    LaunchedEffect(synced) {
        if (synced) rightWidth = leftWidth
    }

    fun Dp.clamp() = coerceIn(PANEL_MIN, PANEL_MAX)

    Row(modifier = Modifier.fillMaxSize()) {
        SidePanel(
            width = leftWidth,
            headings = headings,
            contentListState = contentListState
        )

        DragHandle { delta ->
            with(density) {
                leftWidth = (leftWidth + delta.toDp()).clamp()
                if (synced) rightWidth = leftWidth
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            content()
        }

        DragHandle { delta ->
            with(density) {
                rightWidth = (rightWidth - delta.toDp()).clamp()
                if (synced) leftWidth = rightWidth
            }
        }

        SidePanel(
            width = rightWidth,
            headings = headings,
            contentListState = contentListState
        )
    }
}
