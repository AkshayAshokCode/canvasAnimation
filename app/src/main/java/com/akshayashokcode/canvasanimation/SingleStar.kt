package com.akshayashokcode.canvasanimation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.hypot

class SingleStar
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = R.attr.wavesViewStyle
) : View(context, attrs, defStyleAttr)  {

    private val wavePaint: Paint
    private val waveGap: Float

    private var center = PointF(0f, 0f)
    private var initialRadius = 0f
    private var maxRadius = 0f
    private val wavePath = Path()
    private var waveAnimator: ValueAnimator? = null
    private var waveRadiusOffset = 0f
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        waveAnimator = ValueAnimator.ofFloat(0f, waveGap).apply {
            addUpdateListener {
                waveRadiusOffset = it.animatedValue as Float
            }
            duration = 700L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }
    override fun onDetachedFromWindow() {
        waveAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    init {
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.WavesView, defStyleAttr, 0)

        //init paint with custom attrs
        wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = attrs.getColor(R.styleable.WavesView_waveColor, 0)
            strokeWidth = attrs.getDimension(R.styleable.WavesView_waveStrokeWidth, 0f)
            style = Paint.Style.STROKE
        }

        waveGap = attrs.getDimension(R.styleable.WavesView_waveGap, 50f)
        attrs.recycle()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //set the center of all circles to be center of the view
        center.set(w / 2f, h / 2f)
        maxRadius = Math.hypot(center.x.toDouble(), center.y.toDouble()).toFloat()
        initialRadius = w / waveGap
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw circles separated by a space the size of waveGap
        val path = createStarPath(width/2f, wavePath)
        canvas.drawPath(path, wavePaint)
    }
    private fun createStarPath(
        radius: Float,
        path: Path = Path(),
        points: Int = 20
    ): Path {
        path.reset()
        val pointDelta = 0.7f // difference between the "far" and "close" points from the center
        val angleInRadians = 2.0 * Math.PI / points // essentially 360/20 or 18 degrees, angle each line should be drawn
        val startAngleInRadians = 0.0 //starting to draw star at 0 degrees

        //move pointer to 0 degrees relative to the center of the screen
        path.moveTo(
            center.x + (radius * pointDelta * Math.cos(startAngleInRadians)).toFloat(),
            center.y + (radius * pointDelta * Math.sin(startAngleInRadians)).toFloat()
        )

        //create a line between all the points in the star
        for (i in 1 until points) {
            val hypotenuse = if (i % 2 == 0) {
                //by reducing the distance from the circle every other points, we create the "dip" in the star
                pointDelta * radius
            } else {
                radius
            }

            val nextPointX = center.x + (hypotenuse * Math.cos(startAngleInRadians - angleInRadians * i)).toFloat()
            val nextPointY = center.y + (hypotenuse * Math.sin(startAngleInRadians - angleInRadians * i)).toFloat()
            path.lineTo(nextPointX, nextPointY)
        }

        path.close()
        return path
    }
}