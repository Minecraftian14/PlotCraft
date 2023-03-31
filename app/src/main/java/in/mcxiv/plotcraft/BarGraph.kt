package `in`.mcxiv.plotcraft

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils

class BarGraph : View {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val typedArray: TypedArray

    var plotBackgroundColor: Int
    var plotBarShadowColor: Int = Color.TRANSPARENT
    var plotBarColor: Int
        set(value) {
            field = value
            plotBarShadowColor = ColorUtils.blendARGB(value, Color.BLACK, 0.5f)
        }
    var plotGridColor: Int

    var plotMinimumValue: Float
        set(value) {
            if (value < 0f && value != -1f) throw IllegalArgumentException("Only -1 or any value greater or equal to 0 is accepted. Given $value.")
            field = value
        }

    var plotMaximumValue: Float
        set(value) {
            if (value < 0f && value != -1f) throw IllegalArgumentException("Only -1 or any value greater or equal to 0 is accepted. Given $value.")
            field = value
        }

    var plotBarSpacing: Float
        set(value) {
            if (value < 0f) throw IllegalArgumentException("Value must be positive. Given $value.")
            field = value
        }

    var plotBarWidth: Float
        set(value) {
            if (value < 0f) throw IllegalArgumentException("Value must be positive. Given $value.")
            field = value
        }

    var plotData: List<Number>
        set(value) {
            if (value.isEmpty()) throw Exception("Please give at least one value. Given $value.")
            field = value
        }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

        typedArray = context!!.theme.obtainStyledAttributes(attrs, R.styleable.BarGraph, 0, 0)

        plotBackgroundColor =
            typedArray.getColor(R.styleable.BarGraph_plotBackgroundColor, Color.TRANSPARENT)
        plotBarColor =
            typedArray.getColor(R.styleable.BarGraph_plotBarColor, Color.BLUE)
        plotBarShadowColor =
            typedArray.getColor(R.styleable.BarGraph_plotBarShadowColor, plotBarShadowColor)
        plotGridColor =
            typedArray.getColor(R.styleable.BarGraph_plotGridColor, Color.TRANSPARENT)
        plotMinimumValue =
            typedArray.getFloat(R.styleable.BarGraph_plotMinimumValue, -1f)
        plotMaximumValue =
            typedArray.getFloat(R.styleable.BarGraph_plotMaximumValue, -1f)
        plotBarSpacing =
            typedArray.getFloat(R.styleable.BarGraph_plotBarSpacing, .03f)
        plotBarWidth =
            typedArray.getFloat(R.styleable.BarGraph_plotBarWidth, .1f)

        this.plotData = (1..7).map { (20..80).random() }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        onActuallyDrawing(canvas)
    }

    private fun onActuallyDrawing(canvas: Canvas) {
        if (plotBackgroundColor != Color.TRANSPARENT) {
            paint.apply { color = plotBackgroundColor; style = Paint.Style.FILL }
            canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
        }

        // TODO: Check if this causes a performance issue.
        //  If it does, cache the array post conversion to floats when **setting** plotData
        val maximumValue = if (plotMaximumValue == -1f)
            plotData.maxWith(Comparator.comparingDouble(Number::toDouble)).toFloat()
        else plotMaximumValue
        val minimumValue = if (plotMinimumValue == -1f)
            plotData.minWith(Comparator.comparingDouble(Number::toDouble)).toFloat()
        else plotMinimumValue

        val plotAreaCorrection =
            plotData.size * plotBarWidth + (plotData.size - 1f) * plotBarSpacing
        val barWidth = measuredWidth * plotBarWidth / plotAreaCorrection
        val barSpacing = measuredWidth * plotBarSpacing / plotAreaCorrection

        if (plotGridColor != Color.TRANSPARENT) {
            paint.apply { color = plotGridColor; style = Paint.Style.STROKE; strokeWidth = 2f }
            for (i in 1 until plotData.size) {
                val leftX = i * (barWidth + barSpacing) - barSpacing / 2
                canvas.drawLine(leftX, 0f, leftX, measuredHeight.toFloat(), paint)
            }
        }

        paint.apply { color = plotBarShadowColor; style = Paint.Style.FILL }
        plotData.forEachIndexed { i, v ->
            drawBar(canvas, i * (barWidth + barSpacing), barWidth, 1f)
        }
        paint.apply { color = plotBarColor; style = Paint.Style.FILL }
        plotData.forEachIndexed { i, v ->
            val leftX = i * (barWidth + barSpacing)
            val barHeight = (v.toFloat() - minimumValue) / (maximumValue - minimumValue)
            if (barHeight != 0f)
                drawBar(canvas, leftX, barWidth, barHeight)
        }
    }


    /**
     * @param canvas:
     * @param left_x:
     * @param width: Width of the bar in canvas units.
     * @param height: Height of the bar in fraction! Range [0-1]. 0 is nothing, 1 is as large as measuredHeight and 0.5 is half of measuredHeight.
     */
    private fun drawBar(canvas: Canvas, left_x: Float, width: Float, height: Float) {
        val radius = width / 2f
        val actualHeight = height * (measuredHeight - radius)
        canvas.drawRect(
            /* left =   */ left_x,
            /* top =    */ measuredHeight - actualHeight,
            /* right =  */ left_x + width,
            /* bottom = */ measuredHeight.toFloat(),
            /* paint =  */ paint
        )
        canvas.drawCircle(
            /* cx =     */ left_x + radius,
            /* cy =     */ measuredHeight - actualHeight,
            /* radius = */ radius,
            /* paint =  */ paint
        )
    }

    fun effectiveBarWidth(): Float {
        val plotAreaCorrection =
            plotData.size * plotBarWidth + (plotData.size - 1f) * plotBarSpacing
        return measuredWidth * plotBarWidth / plotAreaCorrection
    }

    fun effectiveBarSpacing(): Float {
        val plotAreaCorrection =
            plotData.size * plotBarWidth + (plotData.size - 1f) * plotBarSpacing
        return measuredWidth * plotBarSpacing / plotAreaCorrection
    }

}