package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tabs.Tab

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TabBar(
    tabs: List<Tab>,
    activeIndex: Int,
    onActivate: (Int) -> Unit,
    onClose: (Int) -> Unit,
    onOpenNew: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val listState = rememberLazyListState()
            LazyRow(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(tabs, key = { it.id }) { tab ->
                    val index = tabs.indexOf(tab)
                    TabItem(
                        title = tab.title,
                        active = index == activeIndex,
                        onActivate = { onActivate(index) },
                        onClose = { onClose(index) }
                    )
                }
            }
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text("Open file (Ctrl+O)") } },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = onOpenNew, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Open file",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TabItem(
    title: String,
    active: Boolean,
    onActivate: () -> Unit,
    onClose: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val bg = when {
        active -> MaterialTheme.colorScheme.surface
        hovered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        else -> Color.Transparent
    }
    val fg = if (active) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .heightIn(min = 28.dp)
            .widthIn(max = 220.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .hoverable(interactionSource)
            .clickable(onClick = onActivate)
            .padding(start = 10.dp, end = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 180.dp)
        )
        Spacer(Modifier.width(4.dp))
        CloseButton(onClick = onClose, tint = fg)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CloseButton(onClick: () -> Unit, tint: Color) {
    val src = remember { MutableInteractionSource() }
    val hovered by src.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (hovered) MaterialTheme.colorScheme.surfaceVariant
                else Color.Transparent
            )
            .hoverable(src)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "Close tab",
            tint = tint,
            modifier = Modifier.size(12.dp)
        )
    }
}
