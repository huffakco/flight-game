package com.example.flightgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

class Bullets (val bulletImage: Bitmap, initialX: Int, initialY: Int, val screenWidth: Int,
               val screenHeight: Int,
               val screenRatioX: Float, val screenRatioY: Float) {

    var speed: Int = 50
    private var imageWidth: Int = bulletImage.width
    private var imageHeight: Int = bulletImage.height
    var x: Int = initialX
    var y: Int = initialY

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bulletImage, x.toFloat(), y.toFloat(), null)
    }

    fun updatePosition() {
        x += (speed * screenRatioX).toInt()
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + imageWidth, y + imageHeight)
    }

}