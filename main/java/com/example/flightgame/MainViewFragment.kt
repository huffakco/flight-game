package com.example.flightgame

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import com.example.flightgame.databinding.FragmentMainViewBinding
import androidx.navigation.findNavController


class MainViewFragment : Fragment() {
    private var _binding: FragmentMainViewBinding? = null
    private val binding get() = _binding!!
    private var logTag = "MainViewFragment"
    private lateinit var prefs: SharedPreferences
    private val outMetrics = DisplayMetrics()
    var screenRatioX: Float = 1.0f
    var screenRatioY: Float = 1.0f
    var isMute: Boolean = false
    private var lastScore: Int = 0
    private lateinit var fragmentContainerView: FragmentContainerView
    //magic screen size numbers from initial drawing developer
    val screenImageSizeX: Float = 1920f
    val screenImageSizeY: Float = 1080f
    private var gameLevel: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            lastScore = (it.getInt("score").toString()).toInt()
        }
        if (lastScore > GameViewFragment.gameLevelWinValue) {
            gameLevel = 1 + (lastScore/GameViewFragment.gameLevelWinValue).toInt()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainViewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireActivity().getSharedPreferences("game", Context.MODE_PRIVATE)
        screenValue()
        screenRatioX = screenImageSizeX / outMetrics.widthPixels
        screenRatioY = screenImageSizeY / outMetrics.heightPixels

        val button: Button = view.findViewById(R.id.play)
        button.setOnClickListener(View.OnClickListener() {
            Log.d(logTag, "play selected")
            val action =   MainViewFragmentDirections.actionMainViewFragmentToGameViewFragment(
                isMute = isMute , gameLevel = gameLevel)
            view.findNavController().navigate(action)
        })

        val highscore: TextView = view.findViewById(R.id.highScoreTxt)
        var lastHighScore: Int = prefs.getInt("highscore", 0)
        if (lastScore > lastHighScore) {
            saveHighScore(lastScore)
            lastHighScore = lastScore
        }
        val highScoreText = getString(R.string.high_score)
        highscore.text = "$highScoreText $lastHighScore"

        val volumeButton: ImageButton = view.findViewById(R.id.volumeCtrl)
        isMute = prefs.getBoolean("isMute", false)
        setButtonImage(volumeButton)

        volumeButton.setOnClickListener() {
            changeMute()
        }

//        var levelText: TextView = view.findViewById(R.id.levelTxt)
//        var levelTextString = getString(R.string.game_level)
//        levelText.text = "$levelTextString $gameLevel"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // from https://stackoverflow.com/questions/63276134/getter-for-defaultdisplay-display-is-deprecated-deprecated-in-java
    private fun screenValue() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            outMetrics.widthPixels = activity?.windowManager?.currentWindowMetrics?.bounds!!.width()
            outMetrics.heightPixels = activity?.windowManager?.currentWindowMetrics?.bounds!!.height()
        } else {
            @Suppress("DEPRECATION")
            val display = activity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getMetrics(outMetrics)
        }
        Log.d(logTag, "$outMetrics.widthPixels $outMetrics.heightPixels")
    }

    private fun saveHighScore(score: Int) {
        var editor: SharedPreferences.Editor = prefs.edit()
        editor.putInt("highscore", score)
        editor.apply()
    }

    private fun setButtonImage(button: ImageButton?) {
        if (isMute) {
            // muted
            button?.setImageResource(R.drawable.ic_baseline_volume_off_24)
        } else {
            button?.setImageResource(R.drawable.ic_baseline_volume_up_24)
        }
    }

    private fun changeMute() {
        isMute = !isMute
        var editor: SharedPreferences.Editor = prefs.edit()
        editor.putBoolean("isMute", isMute)
        editor.apply()

        var volumeButton: ImageButton? = view?.findViewById(R.id.volumeCtrl)
        setButtonImage(volumeButton)
    }
}