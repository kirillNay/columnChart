package nay.kirill.columnchart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import nay.kirill.columnchart.model.Item

class ColumnChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var items: List<Item> = emptyList()

    private val barWidth: Float

    private val barColor: Int

    init {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.ColumnChartView,
            defStyleAttr,
            defStyleRes
        ).apply {
            barWidth = getDimension(R.styleable.ColumnChartView_barWidth, resources.getDimension(R.dimen.column_chart_default_bar_width))
            barColor = getColor(R.styleable.ColumnChartView_barColor, ContextCompat.getColor(context, R.color.chart_bar_default_line_color))

            recycle()
        }
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.column_chart_text_size)
    }

    private val dayOfWeekPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.column_chart_text_size)
        textAlign = Paint.Align.CENTER
    }

    private val levelLinePaint = Paint().apply {
        strokeWidth = resources.getDimension(R.dimen.column_chart_level_line_width)
        color = ContextCompat.getColor(context, R.color.chart_level_line_color)
    }

    private val chartBarPaint = Paint().apply {
        strokeWidth = barWidth
        color = barColor
    }

    private val levelStartPadding: Float = resources.getDimension(R.dimen.column_chart_level_start_padding)

    private val dateTopPadding: Float = resources.getDimension(R.dimen.column_chart_date_top_padding)

    private val maxLevelY: Float get() = textPaint.medium()
    private val minLevelY: Float get() = height - textPaint.fontSize() - dateTopPadding - textPaint.medium()

    private fun levelY(value: Int): Float {
        val levelRate = value / items.maxOf { it.value }.toFloat()
        return minLevelY - (minLevelY - maxLevelY) * levelRate
    }

    private fun barX(number: Int): Float {
        val chartItemWidth = (width - levelStartPadding) / items.size.toFloat()
        return levelStartPadding + chartItemWidth * number + chartItemWidth / 2
    }

    private val bars: Map<Item, RectF> by lazy {
        items.associateWith { item ->
            val centerX = barX(item.dayOfWeek)

            RectF(
                centerX- barWidth / 2F,
                levelY(item.value),
                centerX + barWidth / 2F,
                minLevelY
            )
        }
    }

    override fun onDraw(canvas: Canvas) = with(canvas) {
        drawDayOfWeeks()
        drawLevels()
        drawBars()
    }

    private fun Canvas.drawDayOfWeeks() {
        val days = items.sortedBy { it.dayOfWeek }.map { it.dayOfWeek }
        val y = height.toFloat() - dayOfWeekPaint.descent()

        for (day in days) {
            val x = barX(day)

            drawText(
                resources.getStringArray(R.array.dayOfWeek)[day],
                x,
                y,
                dayOfWeekPaint
            )
        }
    }

    private fun Canvas.drawLevels() {
        val maxValue = items.maxOf { it.value }
        val minValue = 0
        val midValue = (maxValue - minValue) / 2

        val midYBaseline = levelY(midValue)

        listOf(maxLevelY to maxValue, midYBaseline to midValue, minLevelY to minValue).forEach { (baselineY: Float, value: Int) ->
            drawText(
                value.toString(),
                0F,
                baselineY + textPaint.medium() - textPaint.descent(),
                textPaint
            )
            drawLine(levelStartPadding, baselineY, width.toFloat(), baselineY, levelLinePaint)
        }
    }

    private fun Canvas.drawBars() {
        bars.forEach { (_, rect) ->
            drawRect(rect, chartBarPaint)
        }
    }

    private fun Paint.medium() = (descent() - ascent()) / 2

    private fun Paint.fontSize() = (descent() - ascent())

}