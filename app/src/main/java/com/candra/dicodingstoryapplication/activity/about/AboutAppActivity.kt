package com.candra.dicodingstoryapplication.activity.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.data.preferences.ThemePreferences
import com.candra.dicodingstoryapplication.data.preferences.dataStore
import com.candra.dicodingstoryapplication.databinding.ActivityAboutAppBinding
import com.candra.dicodingstoryapplication.themeViewModel.ThemeViewModel
import com.candra.dicodingstoryapplication.viewModelFactory.ThemeViewModelFactory

class AboutAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val themeViewModel = ViewModelProvider(this, ThemeViewModelFactory(pref)).get(
            ThemeViewModel::class.java
        )

        themeViewModel.getTheme().observe(this) { isDark ->
            checkTheme(isDark)
            if (isDark) {
                binding.civAboutApp.setImageResource(R.drawable.dicoding_story_app_white)
            } else {
                binding.civAboutApp.setImageResource(R.drawable.dicoding_story_app_no_background)
            }
        }

        binding.apply {
            ivDicodingLogo.setImageResource(R.drawable.dicoding_logo)
            ivBangkitLogo.setImageResource(R.drawable.bangkit_logo)
            ivKampusMerdekaLogo.setImageResource(R.drawable.kampus_merdeka)
            backToMainFromAboutApp.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun checkTheme(isDark: Boolean) =
        AppCompatDelegate.setDefaultNightMode(if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)

}