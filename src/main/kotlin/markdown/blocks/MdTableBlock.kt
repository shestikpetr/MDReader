package markdown.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import markdown.inline.buildInlineContent
import org.commonmark.ext.gfm.tables.*

@Composable
internal fun MdTable(node: TableBlock) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.extraSmall)
    ) {
        var section = node.firstChild
        while (section != null) {
            when (section) {
                is TableHead -> {
                    var row = section.firstChild
                    while (row != null) {
                        MdTableRow(row as? TableRow, isHeader = true)
                        if (row.next != null || section.next != null) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                        row = row.next
                    }
                }
                is TableBody -> {
                    var row = section.firstChild
                    while (row != null) {
                        MdTableRow(row as? TableRow, isHeader = false)
                        if (row.next != null) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        row = row.next
                    }
                }
            }
            section = section.next
        }
    }
}

@Composable
private fun MdTableRow(row: TableRow?, isHeader: Boolean) {
    if (row == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                if (isHeader) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
            )
    ) {
        var cell = row.firstChild
        while (cell != null) {
            if (cell is TableCell) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = buildInlineContent(cell.firstChild),
                        style = if (isHeader)
                            MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        else
                            MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (cell.next != null) {
                    VerticalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
            cell = cell.next
        }
    }
}
