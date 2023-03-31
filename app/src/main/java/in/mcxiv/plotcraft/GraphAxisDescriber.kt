package `in`.mcxiv.plotcraft

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.ViewGroup

class GraphAxisDescriber : ViewGroup {

    var paint = Paint()
    var descBackgroundColor = Color.TRANSPARENT
        set(value) {
            field = value
            paint.color = value
        }

    var descTitle = "Unset Title"
    var descTitleFont = TextPaint(Paint.ANTI_ALIAS_FLAG)

    var descXAxisLabel = "X - Axis"
    var descYAxisLabel = "Y - Axis"
    var descAxisLabelFont = TextPaint(Paint.ANTI_ALIAS_FLAG)

    lateinit var descXAxisTicks: Array<String>
    lateinit var descYAxisTicks: Array<String>
    var descAxisTicksFont = TextPaint(Paint.ANTI_ALIAS_FLAG)

    var descGraph: BarGraph? = null
    set(value) {
        field = value
        plotData = value!!.plotData
    }

    private val typedArray: TypedArray
    private val rectCache = Rect()

    var plotData: List<Number> = arrayListOf(0)
        set(value) {
            if (descGraph == null) throw java.lang.IllegalStateException()
            field = value
            val graph: BarGraph = descGraph as BarGraph
            graph.plotData = value
            val plotData = graph.plotData
            descXAxisTicks = (1..plotData.size).map { it.toString() }.toTypedArray()
            val minVal = if (graph.plotMinimumValue != -1f) graph.plotMinimumValue
            else plotData.minWith(Comparator.comparingDouble(Number::toDouble)).toFloat()
            val maxVal = if (graph.plotMaximumValue != -1f) graph.plotMaximumValue
            else plotData.maxWith(Comparator.comparingDouble(Number::toDouble)).toFloat()
            descYAxisTicks =
                (0..10).map { (it * (maxVal - minVal) / 10 + minVal).toString() }.toTypedArray()
        }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        typedArray =
            context!!.theme.obtainStyledAttributes(attrs, R.styleable.GraphAxisDescriber, 0, 0)

        descBackgroundColor =
            typedArray.getColor(
                R.styleable.GraphAxisDescriber_descBackgroundColor,
                Color.TRANSPARENT
            )
        descTitle =
            typedArray.getString(R.styleable.GraphAxisDescriber_descTitle) ?: descTitle
        descTitleFont.textSize =
            typedArray.getFloat(R.styleable.GraphAxisDescriber_descTitleFontSize, 50f)
        descTitleFont.color =
            typedArray.getColor(R.styleable.GraphAxisDescriber_descTitleFontColor, Color.WHITE)
        descTitleFont.isFakeBoldText = true

        descXAxisLabel =
            typedArray.getString(R.styleable.GraphAxisDescriber_descXAxisLabel) ?: descXAxisLabel
        descYAxisLabel =
            typedArray.getString(R.styleable.GraphAxisDescriber_descYAxisLabel) ?: descYAxisLabel
        descAxisLabelFont.textSize =
            typedArray.getFloat(R.styleable.GraphAxisDescriber_descAxisLabelFontSize, 40f)
        descAxisLabelFont.color =
            typedArray.getColor(R.styleable.GraphAxisDescriber_descAxisLabelFontColor, Color.WHITE)

        descAxisTicksFont.textSize =
            typedArray.getFloat(R.styleable.GraphAxisDescriber_descAxisTicksFontSize, 30f)
        descAxisTicksFont.color =
            typedArray.getColor(R.styleable.GraphAxisDescriber_descAxisTicksFontColor, Color.WHITE)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount >= 1) {
            val child = getChildAt(0)
            if (child is BarGraph) descGraph = child

            val titleSpace = (40 + descTitleFont.textSize).toInt()

            val yAxisSpace = (40 + descAxisLabelFont.textSize +
                    descYAxisTicks.map { textWidth(it, descTitleFont) }.max()).toInt()

            val xAxisSpace = (40 + descAxisLabelFont.textSize + descAxisTicksFont.textSize).toInt()

            val graphWidth = measuredWidth - 2 * yAxisSpace
            val graphHeight = measuredHeight - titleSpace // - xAxisSpace

            child.measure(
                MeasureSpec.makeMeasureSpec(graphWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(graphHeight - titleSpace, MeasureSpec.AT_MOST)
            )
            child.layout(
                yAxisSpace,
                titleSpace,
                measuredWidth - yAxisSpace,
                measuredHeight - xAxisSpace
            )
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
        super.dispatchDraw(canvas)

        val titleSpace = (40 + descTitleFont.textSize).toInt()
        val maxYTickWidth = descYAxisTicks.map { textWidth(it, descTitleFont) }.max()
        val yAxisSpace = (40 + descAxisLabelFont.textSize +
                maxYTickWidth).toInt()
        val xAxisSpace = (40 + descAxisLabelFont.textSize + descAxisTicksFont.textSize).toInt()
        val graphWidth = measuredWidth - 2 * yAxisSpace
        val graphHeight = measuredHeight - titleSpace - xAxisSpace

        canvas?.drawText(
            descTitle,
            (measuredWidth - textWidth(descTitle, descTitleFont)) / 2f,
            20f + descTitleFont.textSize, descTitleFont
        )

        if (descGraph == null) {
            descXAxisTicks.forEachIndexed { i, s ->
                canvas?.drawText(
                    s,
                    yAxisSpace + graphWidth * (i * 1f / (descXAxisTicks.size)),
                    measuredHeight - 20 - descAxisLabelFont.textSize,
                    descAxisTicksFont
                )
            }
        } else {
            val barWidth = descGraph!!.effectiveBarWidth()
            val barDisplacement = barWidth + descGraph!!.effectiveBarSpacing()
            descXAxisTicks.forEachIndexed { i, s ->
                canvas?.drawText(
                    s,
                    yAxisSpace + barDisplacement * i + (barWidth - textWidth(
                        s,
                        descAxisTicksFont
                    )) / 2,
                    measuredHeight - 20 - descAxisLabelFont.textSize,
                    descAxisTicksFont
                )
            }
        }

        descYAxisTicks.forEachIndexed { i, s ->
            canvas?.drawText(
                s,
                yAxisSpace - 20f - textWidth(s, descAxisTicksFont),
                titleSpace + graphHeight - graphHeight * (i * 1f / descYAxisTicks.size),
                descAxisTicksFont
            )
        }

        canvas?.drawText(
            descXAxisLabel,
            yAxisSpace + (graphWidth - textWidth(descXAxisLabel, descAxisLabelFont)) / 2f,
            measuredHeight - 20f,
            descAxisLabelFont
        )

        canvas?.rotate(-90f)
        canvas?.drawText(
            descYAxisLabel,
            -titleSpace - (graphHeight + textWidth(descYAxisLabel, descAxisLabelFont)) / 2f,
            20 + descAxisLabelFont.textSize,
            descAxisLabelFont
        )
    }

    fun textWidth(text: String, font: Paint): Int {
        font.getTextBounds(text, 0, text.length, rectCache)
        return rectCache.right
    }

    override fun invalidate() {
        super.invalidate()
        descGraph?.invalidate()
    }

}