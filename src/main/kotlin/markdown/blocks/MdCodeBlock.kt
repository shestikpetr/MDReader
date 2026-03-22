package markdown.blocks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import markdown.LocalTextScale
import markdown.highlight.highlight

@Composable
internal fun MdCodeBlock(code: String, language: String = "") {
    val text = remember(code, language) { highlight(code, language) }
    val scale = LocalTextScale.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().hoverable(interactionSource)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = (14 * scale).sp
                )
            )
        }

        if (language.isNotEmpty()) {
            Text(
                text = language,
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        // Language label + copy button row (top-end)
        AnimatedVisibility(
            visible = isHovered,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
        ) {
            FilledTonalIconButton(
                onClick = {
                    clipboard.setText(AnnotatedString(code))
                    copied = true
                    scope.launch { delay(2000); copied = false }
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
