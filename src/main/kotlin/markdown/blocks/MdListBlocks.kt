package markdown.blocks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.commonmark.node.BulletList
import org.commonmark.node.ListItem
import org.commonmark.node.OrderedList

@Composable
internal fun MdBulletList(node: BulletList) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        var item = node.firstChild
        while (item != null) {
            if (item is ListItem) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        modifier = Modifier
                            .padding(top = 3.dp, start = 8.dp, end = 8.dp)
                            .width(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MdBlockNodes(item.firstChild)
                    }
                }
            }
            item = item.next
        }
    }
}

@Composable
internal fun MdOrderedList(node: OrderedList) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        var item = node.firstChild
        var index = node.markerStartNumber
        while (item != null) {
            if (item is ListItem) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "$index.",
                        modifier = Modifier
                            .padding(top = 3.dp, start = 8.dp, end = 8.dp)
                            .width(28.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MdBlockNodes(item.firstChild)
                    }
                }
                index++
            }
            item = item.next
        }
    }
}
