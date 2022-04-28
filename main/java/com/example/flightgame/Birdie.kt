package com.example.flightgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import java.util.*

class Birdie (val birdImages: List<Bitmap>, counter: Int, val screenWidth: Int,
              val screenHeight: Int,
              val screenRatioX: Float, val screenRatioY: Float) {

    var speed: Int = 20
    var wasShot: Boolean = false
    var isGameOver: Boolean = false
    var birdCounter: Int = counter
    private var random: Random = Random()
    private var imageWidth: Int = birdImages[0].width
    private var imageHeight: Int = birdImages[0].height
    var x: Int = screenWidth
    var y: Int = screenHeight
    private val bound: Int = (30 * screenRatioX).toInt()


    init {
        speed = random.nextInt(bound)
        if (speed < (10 * screenRatioX).toInt()) {
            speed = (10 * screenRatioX).toInt()
        }
        y = imageHeight + random.nextInt(screenHeight)
        if (y < imageHeight) {
            y = imageHeight
        }
        if (y > screenHeight - imageHeight) {
            y = screenHeight - imageHeight
        }
    }


    fun draw(canvas: Canvas) {
        if (birdCounter < birdImages.size-1) {
            birdCounter++
            canvas.drawBitmap(birdImages[birdCounter], x.toFloat(), y.toFloat(), null)
        } else {
            birdCounter = -1
            canvas.drawBitmap(birdImages[birdImages.size-1], x.toFloat(), y.toFloat(), null)
        }
    }



    fun updatePosition() {
        x -= speed
        if (x + imageWidth < 0) {
            if (!wasShot) {
                isGameOver = true
                return
            }

            speed = random.nextInt(bound)
            if (speed < (10 * screenRatioX).toInt()) {
                speed = (10 * screenRatioX).toInt()
            }
            x = screenWidth
            y = imageHeight + random.nextInt(screenHeight)
            if (y < imageHeight) {
                y = imageHeight
            }
            if (y > screenHeight - imageHeight) {
                y = screenHeight - imageHeight
            }
            wasShot = false
        }
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + imageWidth, y + imageHeight)
    }

}