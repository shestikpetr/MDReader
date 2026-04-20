package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import markdown.HeadingEntry
import settings.AppSettings

private val PANEL_MIN = 0.dp
private val PANEL_MAX = 600.dp

@Composable
fun ResizableLayout(
    synced: Boolean,
    headings: List<HeadingEntry>,
    contentListState: LazyListState,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    var leftWidth by remember { mutableStateOf(AppSettings.leftPanelWidth.dp) }
    var rightWidth by remember { mutableStateOf(AppSettings.rightPanelWidth.dp) }

    LaunchedEffect(synced) {
        if (synced) rightWidth = leftWidth
    }
    LaunchedEffect(leftWidth, rightWidth) {
        AppSettings.leftPanelWidth = leftWidth.value.toInt()
        AppSettings.rightPanelWidth = rightWidth.value.toInt()
        AppSettings.save()
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
