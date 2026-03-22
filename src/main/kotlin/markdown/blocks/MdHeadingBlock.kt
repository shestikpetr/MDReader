package markdown.blocks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import markdown.inline.buildInlineContent
import org.commonmark.node.Heading

@Composable
internal fun MdHeading(node: Heading) {
    val (textStyle, topPadding) = when (node.level) {
        1    -> MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold) to 16.dp
        2    -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold) to 12.dp
        3    -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold) to 8.dp
        4    -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold) to 4.dp
        5    -> MaterialTheme.typography.titleMedium to 4.dp
        else -> MaterialTheme.typography.titleSmall to 4.dp
    }
    Column {
        if (topPadding > 0.dp) Spacer(Modifier.height(topPadding))
        Text(
            text = buildInlineContent(node.firstChild),
            style = textStyle,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (node.level <= 2) {
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}
