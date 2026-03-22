package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatusBar(wordCount: Int, charCount: Int) {
    Column {
        HorizontalDivider()
        Surface(color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                val readMinutes = (wordCount / 200).coerceAtLeast(1)
                val infoStyle = MaterialTheme.typography.labelSmall
                val infoColor = MaterialTheme.colorScheme.onSurfaceVariant
                Text("$wordCount words", style = infoStyle, color = infoColor)
                Spacer(Modifier.width(12.dp))
                Text("$charCount chars", style = infoStyle, color = infoColor)
                Spacer(Modifier.width(12.dp))
                Text("~$readMinutes min read", style = infoStyle, color = infoColor)
            }
        }
    }
}
