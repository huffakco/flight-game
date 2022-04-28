package com.example.flightgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect


class LevelBird (val birdImages: List<Bitmap>,
        val screenWidth: Int,
        val screenHeight: Int,
                 private val screenRatioX: Float, private val screenRatioY: Float) {

    private var birds: MutableList<Birdie> = arrayListOf()
    var score: Int = 0

    private val logTag = "LevelFlight"

    init {
        for (i in 0 until 4) {
            birds.add(Birdie(birdImages, i, screenWidth, screenHeight, screenRatioX, screenRatioY))
        }
    }

    /**
     * Draws the object on to the canvas.
     */
    fun draw(canvas: Canvas) {
        for (bird in birds) {
            bird.draw(canvas)
        }
    }

    /**
     * update properties for the game object
     */
    fun update() {
        for (bird in birds) {
            bird.updatePosition()
        }

    }

    fun checkCollision(obj: Rect): Boolean {
        var hit = false
        for (bird in birds) {
            if (Rect.intersects(bird.getCollisionShape(), obj)) {
                score++
                bird.x = -500
                bird.wasShot = true
                hit = true
            }
        }
        return hit
    }

    fun checkGameOver(): Boolean {
        for (bird in birds) {
            if (bird.isGameOver) {
                return true
            }
        }
        return false
    }

}