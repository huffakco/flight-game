package com.example.flightgame

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.flightgame.databinding.ActivityMainBinding

// These sources were used to construct this version:
// Reference: https://www.tutorialkart.com/kotlin-android/get-started-with-android-game-development/
// Reference: https://www.youtube.com/watch?v=aTT4GfojkHA
// Reference: https://github.com/heyletscode/2D-Game-In-Android-Studio

class MainActivity : AppCompatActivity() {
    private var logTag = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    lateinit var prefs: SharedPreferences
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        supportActionBar?.hide()

        prefs = this.getSharedPreferences("game", Context.MODE_PRIVATE)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setFullScreen() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

    }

}