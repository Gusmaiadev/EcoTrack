package com.example.ecotrack.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ConsumptionMeterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private var consumption: Float = 0f
    private var maxConsumption: Float = 300f
    private val arrowPath = Path()

    private val colorLow = Color.parseColor("#32CD32")    // Verde
    private val colorMedium = Color.parseColor("#FFD700") // Amarelo
    private val colorHigh = Color.parseColor("#FF4500")   // Vermelho

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 40f
    }

    fun setConsumption(value: Float) {
        consumption = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val diameter = min(width, height)
        val padding = 40f
        val center = width / 2

        // Desenhar borda preta
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 42f
        paint.color = Color.BLACK
        rectF.set(
            padding,
            padding,
            diameter - padding,
            diameter - padding
        )
        canvas.drawArc(rectF, 180f, 180f, false, paint)

        // Configurar retângulo para arcos coloridos
        paint.strokeWidth = 40f
        rectF.set(
            padding + 1,
            padding + 1,
            diameter - padding - 1,
            diameter - padding - 1
        )

        // Desenhar os três segmentos do arco
        val segmentAngle = 60f

        // Segmento Verde
        paint.color = colorLow
        canvas.drawArc(rectF, 180f, segmentAngle, false, paint)

        // Segmento Amarelo
        paint.color = colorMedium
        canvas.drawArc(rectF, 180f + segmentAngle, segmentAngle, false, paint)

        // Segmento Vermelho
        paint.color = colorHigh
        canvas.drawArc(rectF, 180f + (segmentAngle * 2), segmentAngle, false, paint)

        // Calcular posição do ponteiro
        val angle = 180f + (consumption / maxConsumption) * 180f
        val angleRadians = Math.toRadians(angle.toDouble())

        val centerY = height / 2 + 20 // Ajustado para ficar mais próximo do arco

        // Círculo central do ponteiro
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        canvas.drawCircle(center, centerY, 10f, paint)

        // Desenhar ponteiro
        val pointerLength = (diameter - padding * 2) / 2 - 5 // Reduzido para ficar mais próximo
        val tipX = center + (pointerLength * cos(angleRadians)).toFloat()
        val tipY = centerY + (pointerLength * sin(angleRadians)).toFloat()

        paint.strokeWidth = 8f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(center, centerY, tipX, tipY, paint)

        // Valor do consumo
        paint.style = Paint.Style.FILL
        paint.textSize = diameter / 8
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.BLACK
        val consumptionText = "${String.format("%.0f", consumption)} kWh"
        canvas.drawText(consumptionText, width / 2, height - height * 0.15f, paint)
    }
}