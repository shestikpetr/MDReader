package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppToolbar(
    title: String,
    synced: Boolean,
    textScale: Float,
    isDarkTheme: Boolean,
    onSyncedChange: (Boolean) -> Unit,
    onOpenFile: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomReset: () -> Unit,
    onDarkThemeChange: (Boolean) -> Unit
) {
    Surface(
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Open file
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text("Open file (Ctrl+O)") } },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = onOpenFile) {
                    Icon(
                        Icons.Default.FolderOpen,
                        contentDescription = "Open file",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 2.dp))

            // File name
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )

            // Zoom out
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text("Zoom out (Ctrl+−)") } },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = onZoomOut, enabled = textScale > 0.5f) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Zoom out",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Zoom % — double-click to reset
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text("Double-click to reset (Ctrl+0)") } },
                state = rememberTooltipState()
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(min = 44.dp)
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onDoubleClick = onZoomReset,
                            onClick = {}
                        )
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(textScale * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Zoom in
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text("Zoom in (Ctrl++)") } },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = onZoomIn, enabled = textScale < 3f) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Zoom in",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 2.dp))

            // Dark theme
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text(if (isDarkTheme) "Light mode" else "Dark mode") } },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = { onDarkThemeChange(!isDarkTheme) }) {
                    Icon(
                        if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle theme",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 2.dp))

            // Sync panels toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Checkbox(checked = synced, onCheckedChange = onSyncedChange)
                Text(
                    text = "Sync",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}
