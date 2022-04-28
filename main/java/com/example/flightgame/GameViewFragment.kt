package com.example.flightgame

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.flightgame.databinding.FragmentGameViewBinding

/**
 * A simple [Fragment] subclass.
 * Use the [GameViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameViewFragment : Fragment(), GameOverObserver{

    private var _binding: FragmentGameViewBinding? = null
    private val binding get() = _binding!!
    private var logTag = "GameViewFragment"
    private var levelView: LevelView? = null

    private var gameLevel: Int = 1
    private var gameRunning: Boolean = true
    companion object GameViewFragmentData  {
        var screenX: Int = Resources.getSystem().displayMetrics.widthPixels
        var screenY: Int = Resources.getSystem().displayMetrics.heightPixels
        // magic numbers for image size from the originally drawing developer
        var screenRatioX: Float = 1920f / screenX  // should this be 1.0f
        var screenRatioY: Float = 1080f / screenY
        var isMute: Boolean = false
        var gameLevel: Int = 1
        var gameLevelWinValue: Int = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

            GameViewFragmentData.isMute = (it.getBoolean("isMute").toString()).toBoolean()
            GameViewFragmentData.gameLevel = (it.getInt("gameLevel")).toString().toInt()
        }
        val currentOrientation = resources.configuration.orientation
        Log.d(logTag, "OnCreate: current orientation: $currentOrientation")
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            Log.d(logTag, "Landscape in OnCreate")
        } else {
            // Portrait
            Log.d(logTag, "Portrait in OnCreate")
        }
//        Log.d(logTag, "$screenRatioX $screenRatioY")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameViewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(logTag, "onviewCreated")
        levelView = view.findViewById<LevelView>(R.id.surfaceView)
        levelView?.observers?.add(this)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun updateGame() {
        Log.d(logTag, "game over in fragment observed")
        if (gameRunning) {
            var action = levelView?.let {
                GameViewFragmentDirections.actionGameViewFragmentToMainViewFragment(
                    score = it.gameOverScore
                )
            }
            action?.let { view?.findNavController()?.navigate(it) }
        }
        gameRunning = false
    }

    override fun onPause() {
        super.onPause()
        Log.d(logTag, "pause observed")
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onResume() {
        super.onResume()

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val currentOrientation = resources.configuration.orientation

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            Log.d(logTag, "Landscape in onResume")
        } else {
            // Portrait
            Log.d(logTag, "Portrait in onResume")
        }

        screenX = Resources.getSystem().displayMetrics.widthPixels
        screenY = Resources.getSystem().displayMetrics.heightPixels
        if (screenX < screenY) {
            val tmp: Int = screenX
            screenX = screenY
            screenY = tmp
        }
        screenRatioX = 1920f / screenX  // should this be 1.0f
        screenRatioY = 1080f / screenY
        Log.d(logTag, "onResume: current orientation: $currentOrientation $screenX $screenY $screenRatioX $screenRatioY")

    }

}