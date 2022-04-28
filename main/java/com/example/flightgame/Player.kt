package com.example.flightgame

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Player Class.
 */

class Player(private val image: Bitmap, screenWidth: Int, screenHeight: Int) {
    private var x: Int = 0
    private var y: Int = 0
    private val w: Int
    private val h: Int
    private val screenWidth = screenWidth
    private val screenHeight = screenHeight

    init {
        w = image.width
        h = image.height

        x = screenWidth/2
        y = screenHeight - 200
    }

    /**
     * Draws the object on to the canvas.
     */
    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }

    /**
     * update properties for the game object
     * when the player touches the screen, position the player bitmap there
     */
    fun update(touch_x: Int, touch_y: Int) {
        x = touch_x - w / 2
        y = touch_y - h / 2
    }

}