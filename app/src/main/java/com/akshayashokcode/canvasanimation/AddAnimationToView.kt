package com.akshayashokcode.canvasanimation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*

class AddAnimationToView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.wavesViewStyle,
) : View(context, attrs, defStyleAttr) {
    private var colorRandom: Int;
    private val rnd: Random = Random();
    private val wavePaint: Paint
    private val waveGap: Float

    private var center = PointF(0f, 0f)
    private var initialRadius = 0f
    private var maxRadius = 0f

    private var waveAnimator: ValueAnimator? = null
    private var waveRadiusOffset = 0f
        set(value) {
            field = value
            postInvalidateOnAnimation()
            colorRandom = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

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
        colorRandom = attrs.getColor(R.styleable.WavesView_waveColor, 0)
        //init paint with custom attrs
        wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorRandom
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
        var currentRadius = initialRadius + waveRadiusOffset


        while (currentRadius < maxRadius) {
            wavePaint.color = colorRandom
            canvas.drawCircle(center.x, center.y, currentRadius, wavePaint)
            currentRadius += waveGap
            colorRandom = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        }
    }
}