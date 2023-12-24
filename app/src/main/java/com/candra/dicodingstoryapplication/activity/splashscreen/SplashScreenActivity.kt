package com.candra.dicodingstoryapplication.activity.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.activity.home.HomeActivity
import com.candra.dicodingstoryapplication.activity.main.MainActivity
import com.candra.dicodingstoryapplication.activity.main.MainViewModel
import com.candra.dicodingstoryapplication.data.preferences.ThemePreferences
import com.candra.dicodingstoryapplication.data.preferences.dataStore
import com.candra.dicodingstoryapplication.databinding.ActivitySplashScreenBinding
import com.candra.dicodingstoryapplication.themeViewModel.ThemeViewModel
import com.candra.dicodingstoryapplication.viewModelFactory.MainViewModelFactory
import com.candra.dicodingstoryapplication.viewModelFactory.ThemeViewModelFactory

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private var nightMode: Boolean = true
    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModelFactory.getInstance(this@SplashScreenActivity, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val themeViewModel = ViewModelProvider(this, ThemeViewModelFactory(pref))[ThemeViewModel::class.java]

        themeViewModel.getTheme().observe(this) { isDark ->
            checkTheme(isDark)
            if (isDark) {
                binding.logoApp.setImageResource(R.drawable.dicoding_story_app_white)
            } else {
                binding.logoApp.setImageResource(R.drawable.dicoding_story_app_no_background)
            }
        }

        binding.apply {
            ivDicodingLogo.setImageResource(R.drawable.dicoding_logo)
            ivBangkitLogo.setImageResource(R.drawable.bangkit_logo)
            ivKampusMerdekaLogo.setImageResource(R.drawable.kampus_merdeka)
        }
        goToActivity()
    }

    private fun checkTheme(isDark: Boolean) =
        AppCompatDelegate.setDefaultNightMode(if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)


    private fun goToActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            mainViewModel.getSessionToken().observe(this) {
                if (!it.isUserLogin) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }, DURATION_DELAY)
    }


    companion object {
        private const val DURATION_DELAY: Long = 2000
    }
}