package com.example.flightgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect


/**
 * Grenade Class.
 * It could be considered as System. System is playing against you in the game.
 * Grenade is the opponent.
 */

class Grenade(var image: Bitmap, screenWidth: Int, screenHeight: Int) {
    var x: Int = 0
    var y: Int = 0
    var w: Int = 0
    var h: Int = 0
    private var xVelocity = 20
    private var yVelocity = 20
    private val screenWidth = screenWidth
    private val screenHeight = screenHeight

    init {
        w = image.width/2
        h = image.height/2

        x=screenWidth/2
        y=screenHeight/2
    }

    /**
     * Draws the object on to the canvas.
     */
    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }

    /**
     * update properties for the game object
     */
    fun update() {
        // val randomNum = ThreadLocalRandom.current().nextInt(1, 5)

        if (x > screenWidth - image.width || x < image.width) {
            xVelocity = xVelocity * -1
        }
        if (y > screenHeight - image.height || y < image.height) {
            yVelocity = yVelocity * -1
        }

        x += (xVelocity)
        y += (yVelocity)
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + 20, y + 20)
    }


    fun checkCollision(obj: Rect): Boolean {
        var hit = false
        if (Rect.intersects(getCollisionShape(), obj)) {
                hit = true
            }
        return hit
    }

}