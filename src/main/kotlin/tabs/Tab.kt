package tabs

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import markdown.HeadingEntry
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class Tab(
    val file: File,
    initialContent: String,
    initialHeadings: List<HeadingEntry>
) {
    val id: Long = nextId.getAndIncrement()
    var content: String by mutableStateOf(initialContent)
    var headings: List<HeadingEntry> by mutableStateOf(initialHeadings)
    val lazyListState: LazyListState = LazyListState()
    val title: String get() = file.name

    companion object {
        private val nextId = AtomicLong(1)
    }
}
