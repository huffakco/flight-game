package com.example.flightgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log

class LevelFlight (flightImages: List<Bitmap>,
                   val screenWidth: Int,
                   val screenHeight: Int,
                   screenRatioX: Float, screenRatioY: Float) {

    var flight1: Bitmap = flightImages[0]
    var flight2: Bitmap = flightImages[1]
    private var isFlight: Boolean = true
    var dead: Bitmap = flightImages[flightImages.size-1]
    var shoot: MutableList<Bitmap> = arrayListOf<Bitmap>()
    var y: Int = screenHeight/2
    var x: Int = (64 * screenRatioX).toInt()
    val velocityY = 30f * screenRatioY
    var toShoot: Int = 0
    var shootCounter: Int = -1
    var newBullet: Boolean = false
    var isGoingUp: Boolean = false
    var width: Int = flightImages[0].width
    var height: Int = flightImages[0].height
    private val logTag = "LevelFlight"

    init {
        if (flightImages.size < 3) {
            Log.d(logTag, "not enough images sent")
        }

        for (i in 2..(flightImages.size-2)) {
            shoot.add(flightImages[i])
        }
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }

    fun draw(canvas: Canvas) {
        if (toShoot > 0) {
            drawShoot(canvas)
        } else {
            drawFlight(canvas)
        }
    }

    /**
     * Draws the object on to the canvas.
     */
    private fun drawFlight(canvas: Canvas) {
        if (isFlight) {
            canvas.drawBitmap(flight1, x.toFloat(), y.toFloat(), null)
        } else {
            canvas.drawBitmap(flight2, x.toFloat(), y.toFloat(), null)
        }
        isFlight = !isFlight
    }

    fun drawDead(canvas: Canvas) {
        canvas.drawBitmap(dead, x.toFloat(), y.toFloat(), null)
    }

    private fun drawShoot(canvas: Canvas) {
        if (shootCounter < shoot.size-1) {
            ++shootCounter
            canvas.drawBitmap(shoot[shootCounter], x.toFloat(), y.toFloat(), null)
        } else {
            shootCounter = -1
            toShoot--
            newBullet = true
            canvas.drawBitmap(shoot[shoot.size - 1], x.toFloat(), y.toFloat(), null)
        }
    }

    fun update() {
        if (isGoingUp) {
            y -= (velocityY).toInt()
        } else {
            y += (velocityY).toInt()
        }
        if (y < 0) { y = 0 }
        if (y >= screenHeight - height) { y = screenHeight - height }

    }

}