package com.example.myaipeopletracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.face.Face

class FaceOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var faces: List<Face> = emptyList()
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1
    private var isFrontMode: Boolean = true // Флаг отзеркаливания

    private val boxPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 8.0f
    }

    private val textPaint = Paint().apply {
        color = Color.RED
        textSize = 60.0f
        style = Paint.Style.FILL
        isFakeBoldText = true
    }

    fun setFaces(faces: List<Face>, imageWidth: Int, imageHeight: Int, isFrontMode: Boolean) {
        this.faces = faces
        this.imageWidth = imageWidth
        this.imageHeight = imageHeight
        this.isFrontMode = isFrontMode
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (faces.isEmpty()) return

        val scaleX = width.toFloat() / imageHeight.toFloat()
        val scaleY = height.toFloat() / imageWidth.toFloat()

        for (face in faces) {
            val boundingBox = face.boundingBox

            // Базовое масштабирование
            val rawLeft = boundingBox.left * scaleX
            val rawRight = boundingBox.right * scaleX
            val top = boundingBox.top * scaleY
            val bottom = boundingBox.bottom * scaleY

            // Если камера фронтальная, отзеркаливаем координаты по оси X
            val left = if (isFrontMode) width - rawRight else rawLeft
            val right = if (isFrontMode) width - rawLeft else rawRight

            val rect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            canvas.drawRect(rect, boxPaint)

            val id = face.trackingId
            val text = if (id != null) "Person ID: $id" else "Unknown"
            canvas.drawText(text, left, top - 15, textPaint)
        }
    }
}