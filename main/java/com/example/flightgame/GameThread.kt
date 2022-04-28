package com.example.flightgame

import android.graphics.Canvas
import android.os.Build
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi

/**
 * Created by arjun on 26/12/17.
 */

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: LevelView) : Thread() {
    private var running: Boolean = false

    private val targetFPS = 50 // frames per second, the rate at which you would like to refresh the Canvas

    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        val targetTime = (1000 / targetFPS).toLong()

        while (running) {
            startTime = System.nanoTime()
            canvas = null

            try {
                // locking the canvas allows us to draw on to it
                canvas = this.surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    this.gameView.update()
                    this.gameView.draw(canvas!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = targetTime - timeMillis
            if (waitTime < 0) {
                waitTime = 10
            }


            try {
                sleep(waitTime)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private var canvas: Canvas? = null
    }

}