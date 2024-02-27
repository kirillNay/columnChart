package nay.kirill.columnchart.model

import androidx.annotation.IntRange
import kotlin.random.Random

data class Item(
    val value: Int,
    @IntRange(0, 6) val dayOfWeek: Int
)

val mockData: List<Item>
    get() = List(6) {
        Item(Random(it).nextInt(15), it)
    }