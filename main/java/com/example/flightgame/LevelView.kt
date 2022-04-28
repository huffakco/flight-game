package com.example.flightgame

import android.content.Context
import android.graphics.*
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.M)
class LevelView (context: Context, attributes: AttributeSet) : SurfaceView(context, attributes),
    SurfaceHolder.Callback, GameOverObservable {
        override val observers: ArrayList<GameOverObserver> = ArrayList()
        private var thread: GameThread
        private var paint: Paint = Paint()
        private var grenade: Grenade? = null
//        private var player: Player? = null
        private var levelBackground: LevelBackground? = null
        private var levelFlight: LevelFlight? = null
        private var levelBird: LevelBird? = null
        private var levelBullet: LevelBullet? = null
        private var flightImages: MutableList<Bitmap> = arrayListOf<Bitmap>()
        private var birdImages: MutableList<Bitmap> = arrayListOf<Bitmap>()
        private var touched: Boolean = false
        private var touched_x: Int = 0
        private var touched_y: Int = 0
        var isGameOver: Boolean = false
        var gameOverScore: Int by Delegates.observable(0) { _, _, _ ->
            isGameOver = true
        }
        var screenWidth: Int = GameViewFragment.screenX
        var screenHeight: Int = GameViewFragment.screenY
        var screenRatioX: Float =  GameViewFragment.screenRatioX
        var screenRatioY: Float = GameViewFragment.screenRatioY
        var logTag = "LevelView"

        private var sound1: Int
        private var sound2: Int
        private var sound3: Int
        private var sound4: Int
        private var soundpool: SoundPool
        private var isMute: Boolean = GameViewFragment.isMute
        private var level: Int = GameViewFragment.gameLevel

        init {
            var attr = attributes.positionDescription
            Log.d(logTag,"init: $attributes $attr" )
            // add callback
            holder.addCallback(this)

            // instantiate the game thread
            thread = GameThread(holder, this)
            paint.color = resources.getColor(R.color.white, context.theme)
            paint.textSize = 96f * screenRatioY
            Log.d(logTag, "init: screenRatioX $screenRatioX screenRatioY $screenRatioY width: $screenWidth height $screenHeight")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var audioAttributes: AudioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()

                soundpool = SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build()
            } else {

                soundpool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
            }
            sound3 = soundpool.load(this.context, R.raw.sound2, 1)
            sound1 = soundpool.load(this.context,R.raw.shoot, 2)
            sound2 = soundpool.load(this.context, R.raw.sound1, 3)
            sound4 = soundpool.load(this.context, R.raw.big_explosion_cut_off, 1)

        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {
            var retry = true
            while (retry) {
                try {
                    thread.setRunning(false)
                    thread.join()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                retry = false
            }
        }

        override fun surfaceCreated(p0: SurfaceHolder) {
            // background

            var levelBackgroundBitmap =
                BitmapFactory.decodeResource(resources, R.drawable.background)
            levelBackgroundBitmap = Bitmap.createScaledBitmap(
                levelBackgroundBitmap,
                screenWidth,
                screenHeight,
                false
            )
            levelBackground = LevelBackground(
                levelBackgroundBitmap, screenWidth,
                screenHeight, screenRatioX, screenRatioY
            )
            // game objects
            buildFlightImages()
            levelFlight = LevelFlight(flightImages, screenWidth, screenHeight, screenRatioX, screenRatioY)

            buildBirdImages()
            levelBird = LevelBird(birdImages, screenWidth, screenHeight,screenRatioX, screenRatioY)

            if (level > 1) {
                var grenadeBitmap = BitmapFactory.decodeResource(resources, R.drawable.grenade)

                grenade = Grenade(grenadeBitmap, screenWidth, screenHeight)
            }

            levelBullet = LevelBullet(
                buildBulletImage(),
                screenWidth,
                screenHeight,
                screenRatioX,
                screenRatioY
            )

            // this is the bird added as a touch event from https://www.tutorialkart.com/kotlin-android/get-started-with-android-game-development/
//            var playerBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird1)
//            var width = ((playerBitmap.width/4) * screenRatioX).toInt()
//            var height = ((playerBitmap.height/4) * screenRatioY).toInt()
//            playerBitmap = Bitmap.createScaledBitmap(playerBitmap, width, height, false)
//            player = Player(playerBitmap, screenWidth, screenHeight )
            if (thread.state === Thread.State.TERMINATED) {
                thread = GameThread(holder, this)
            }
            // start the game thread
            thread.setRunning(true)
            thread.start()
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            Log.d(logTag, "surface changed")
        }


        /**
         * Function to update the positions of player and game objects
         */
        fun update() {
            levelBackground!!.update()

            levelFlight!!.update()
//            levelBullet!!.update()
            if (levelFlight!!.newBullet) {
                // add a bullet
                levelBullet!!.addBullet(levelFlight!!.x + flightImages[0].width,
                    levelFlight!!.y)
                levelFlight!!.newBullet = false
                if (!isMute) {
                        soundpool.play(sound1, 1f, 1f, 0, 0, 1f)
                }
            }

            levelBird!!.update()


            levelBullet!!.update()

            if (level > 1) {
                grenade!!.update()
                var hits = grenade!!.checkCollision(levelFlight!!.getCollisionShape())
                if (!isMute) {
                    if (hits) {
                        soundpool.play(sound4, 1f, 1f, 0, 0, 1f)
                    }
                }
                isGameOver = isGameOver or hits
            }

            for (bullet in levelBullet!!.bulletsList) {
                var hits = levelBird!!.checkCollision(bullet.getCollisionShape())
                if (!isMute) {
                    if (hits) {
                        soundpool.play(sound2, 1f, 1f, 0, 0, 1f)
                    }
                }
            }
            isGameOver = isGameOver or levelBird!!.checkCollision(levelFlight!!.getCollisionShape())
            isGameOver = isGameOver or levelBird!!.checkGameOver()
            // this is moving the bird to touched location
//            if(touched) {
//                player!!.update(touched_x, touched_y)
//            }
        }

        /**
         * Everything that has to be drawn on Canvas
         */
        override fun draw(canvas: Canvas) {
            super.draw(canvas)
            var currentScore = levelBird!!.score

            if (isGameOver) {
                levelBackground!!.draw(canvas)
                levelBird!!.draw(canvas)
                levelFlight!!.drawDead(canvas)
                canvas.drawText("$currentScore", screenWidth/2f, 200f, paint)
                updateGame()
            } else {
                levelBackground!!.draw(canvas)
                levelFlight!!.draw(canvas)
                levelBird!!.draw(canvas)
                levelBullet!!.draw(canvas)

                if (level > 1) {
                    grenade!!.draw(canvas)
                }

                canvas.drawText("$currentScore", screenWidth/2f, 200f, paint)
            }

            // drawing the bird at touched location
//            player!!.draw(canvas)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            // whenever there is a touch on the screen,
            // we can get the position of touch
            // which we may use it for tracking some of the game objects
            touched_x = event.x.toInt()
            touched_y = event.y.toInt()

            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.x < screenWidth / 2) {
                        levelFlight?.isGoingUp = true
                    }
                }
                MotionEvent.ACTION_MOVE -> touched = true
                MotionEvent.ACTION_UP -> {
                    levelFlight!!.isGoingUp = false
                    if (event.x > screenWidth / 2) {
                        levelFlight!!.toShoot++
                    }
                }
                MotionEvent.ACTION_CANCEL -> touched = false
                MotionEvent.ACTION_OUTSIDE -> touched = false
                else -> {
                    super.onTouchEvent(event)
                }
            }
            return true
        }

    private fun buildFlightImages() {

        flightImages.add(BitmapFactory.decodeResource(resources,R.drawable.fly1))
        flightImages.add(BitmapFactory.decodeResource(resources,R.drawable.fly2))
        flightImages.add(BitmapFactory.decodeResource(resources, R.drawable.shoot1))
        flightImages.add(BitmapFactory.decodeResource(resources, R.drawable.shoot2))
        flightImages.add( BitmapFactory.decodeResource(resources, R.drawable.shoot3))
        flightImages.add(BitmapFactory.decodeResource(resources, R.drawable.shoot4))
        flightImages.add(BitmapFactory.decodeResource(resources, R.drawable.shoot5))
        flightImages.add(BitmapFactory.decodeResource(resources, R.drawable.dead))
        var width = ((BitmapFactory.decodeResource(resources,R.drawable.fly1).width/4) * screenRatioX).toInt()
        var height = ((BitmapFactory.decodeResource(resources,R.drawable.fly1).height/4) * screenRatioY).toInt()
        for (i in 0 until flightImages.size) {
            flightImages[i] = Bitmap.createScaledBitmap(flightImages[i],
                width, height, false)
        }
    }


    private fun buildBirdImages() {
        birdImages.add(BitmapFactory.decodeResource(resources,R.drawable.bird1))
        birdImages.add(BitmapFactory.decodeResource(resources,R.drawable.bird2))
        birdImages.add(BitmapFactory.decodeResource(resources, R.drawable.bird3))
        birdImages.add(BitmapFactory.decodeResource(resources, R.drawable.bird4))
        var width = ((BitmapFactory.decodeResource(resources,R.drawable.bird1).width/8) * screenRatioX).toInt()
        var height = ((BitmapFactory.decodeResource(resources,R.drawable.bird1).height/8) * screenRatioY).toInt()
        for (i in 0 until birdImages.size) {
            birdImages[i] = Bitmap.createScaledBitmap(birdImages[i],
                width, height, false)
        }
        Log.d(logTag, "screen size when birds scaled: $width $height")
    }

    private fun buildBulletImage(): Bitmap {
        var bulletImage = BitmapFactory.decodeResource(resources, R.drawable.bullet)
        var width = ((bulletImage.width/4) * screenRatioX).toInt()
        var height = ((bulletImage.height/4) * screenRatioY).toInt()
        bulletImage = Bitmap.createScaledBitmap(bulletImage,
            width, height, false)
        return bulletImage
    }

    override fun updateGame() {
        Log.d(logTag, "game over")
        if (!isMute) {
            soundpool.play(sound3, 1f, 1f, 0, 0, 1f)
        }
        Thread.sleep(1000)
        gameOverScore = levelBird!!.score
        sendUpdateEvent()
    }

}
