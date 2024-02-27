package nay.kirill.columnchart.model

import androidx.annotation.IntRange

data class Item(
    val value: Int,
    @IntRange(0, 6) val dayOfWeek: Int
)

val mockData: List<Item>
    get() = List(7) {
        Item((0..10).random(), it)
    }