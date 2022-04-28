package com.example.flightgame

import android.graphics.Bitmap
import android.graphics.Canvas

class LevelBackground(var image: Bitmap,
                      screenWidth: Int, screenHeight: Int,
                      screenRatioX: Float, screenRatioY: Float) {
    var b1X: Int = 0
    var b2X: Int = screenWidth
    var y: Int = 0
    var w: Int = 0
    var h: Int = 0
    private var xVelocity = 10f * screenRatioX
    private var yVelocity = 0f
    private val screenWidth = screenWidth
    private val screenHeight = screenHeight
    private val screenRatioX = screenRatioX
    private val screenRatioY = screenRatioY
    private var background1: Bitmap = image
    private var background2: Bitmap = image


    init {
        w = image.width
        h = image.height

        b1X=0
        b2X=screenWidth-3
        y=0
    }

    /**
     * Draws the object on to the canvas.
     */
    fun draw(canvas: Canvas) {
        canvas.drawBitmap(background1, b1X.toFloat(), y.toFloat(), null)
        canvas.drawBitmap(background2, b2X.toFloat(), y.toFloat(), null)
    }

    /**
     * update properties for the game object
     */
    fun update() {
        b1X -= (xVelocity).toInt()
        b2X -= (xVelocity).toInt()
        if (b1X + w <= 0) {
            b1X = b2X + screenWidth-3
        }
        if (b2X + w <= 0) {
            b2X = b1X + screenWidth-3
        }

    }
}