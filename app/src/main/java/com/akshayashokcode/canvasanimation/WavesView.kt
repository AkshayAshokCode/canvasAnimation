package com.akshayashokcode.canvasanimation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class WavesView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = R.attr.wavesViewStyle
) : View(context, attrs, defStyleAttr) {

    private val wavePaint: Paint
    private val waveGap: Float

    private var maxRadius = 0f
    private var center = PointF(0f, 0f)
    private var initialRadius = 0f

    init {
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.WavesView, defStyleAttr, 0)

        //init paint with custom attrs
        wavePaint = Paint(ANTI_ALIAS_FLAG).apply {
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
        var currentRadius = initialRadius
        while (currentRadius < maxRadius) {
            canvas.drawCircle(center.x, center.y, currentRadius, wavePaint)
            currentRadius += waveGap
        }
    }
}