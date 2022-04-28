package com.example.flightgame

import android.graphics.Bitmap
import android.graphics.Canvas

class LevelBullet (val bulletImage: Bitmap,
                   val screenWidth: Int,
                   val screenHeight: Int,
                   val screenRatioX: Float, val screenRatioY: Float) {

    var width: Int = bulletImage.width
    var height: Int = bulletImage.height
    var bulletsList: MutableList<Bullets> = arrayListOf()

    private val logTag = "LevelFlight"

    fun addBullet(posX: Int, posY: Int) {
        bulletsList.add(Bullets(bulletImage, posX, posY, screenWidth, screenHeight, screenRatioX, screenRatioY))
    }


    fun draw(canvas: Canvas) {
        for (bullet in bulletsList) {
            bullet.draw(canvas)
        }
    }

    fun update() {
        var trash = arrayListOf<Bullets>()
        for (bullet in bulletsList) {
            bullet.updatePosition()
            if (bullet.x > screenWidth) {
                trash.add(bullet)
            }
        }
        // remove bullets that are out of view
        for (bullet in trash) {
            bulletsList.remove(bullet)
        }
    }

}