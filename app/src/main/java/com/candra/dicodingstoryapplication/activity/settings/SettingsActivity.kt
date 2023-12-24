package com.candra.dicodingstoryapplication.activity.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.activity.about.AboutAppActivity
import com.candra.dicodingstoryapplication.activity.about.AboutDevActivity
import com.candra.dicodingstoryapplication.activity.login.LoginActivity
import com.candra.dicodingstoryapplication.data.preferences.ThemePreferences
import com.candra.dicodingstoryapplication.data.preferences.dataStore
import com.candra.dicodingstoryapplication.databinding.ActivitySettingsBinding
import com.candra.dicodingstoryapplication.themeViewModel.ThemeViewModel
import com.candra.dicodingstoryapplication.viewModelFactory.StoryViewModelFactory
import com.candra.dicodingstoryapplication.viewModelFactory.ThemeViewModelFactory

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var mode: Boolean = false
    private val settingsViewModel by viewModels<SettingsViewModel> {
        StoryViewModelFactory.getInstance(this@SettingsActivity, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val themeViewModel = ViewModelProvider(this, ThemeViewModelFactory(pref)).get(
            ThemeViewModel::class.java
        )

        themeViewModel.getTheme().observe(this) { isDark ->
            if (isDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                mode = true
                binding.apply {
                    fabMode.setImageResource(R.drawable.light_mode)
                    tvMode.text = getString(R.string.light_mode)
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                mode = false
                binding.apply {
                    fabMode.setImageResource(R.drawable.night_mode)
                    tvMode.text = getString(R.string.night_mode)
                }
            }
        }

        binding.apply {
            backToMainFromAddStory.setOnClickListener {
                onBackPressed()
            }
            fabAboutApp.setOnClickListener {
                startActivity(Intent(this@SettingsActivity, AboutAppActivity::class.java))
            }

            fabAboutDev.setOnClickListener {
                startActivity(Intent(this@SettingsActivity, AboutDevActivity::class.java))
            }

            fabLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            fabMode.setOnClickListener {
                setTheme(mode, themeViewModel)
            }

            actionLogout.setOnClickListener {
                val sweetAlertDialog = SweetAlertDialog(
                    this@SettingsActivity,
                    SweetAlertDialog.WARNING_TYPE
                ).setTitleText(getString(R.string.logout))
                    .setContentText(getString(R.string.question_logout))
                    .setConfirmButtonBackgroundColor(
                        ContextCompat.getColor(
                            this@SettingsActivity, R.color.navy
                        )
                    ).setCancelButtonBackgroundColor(
                        ContextCompat.getColor(
                            this@SettingsActivity, R.color.navy
                        )
                    ).setConfirmButton("OK") {
                        settingsViewModel.logout()
                        val sweetAlertDialog = SweetAlertDialog(
                            this@SettingsActivity, SweetAlertDialog.SUCCESS_TYPE
                        ).setTitleText(getString(R.string.logout_success))
                            .setContentText(getString(R.string.logout_success_text))
                            .setConfirmButton("OK") {
                                val intent = Intent(
                                    this@SettingsActivity, LoginActivity::class.java
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }.setConfirmButtonBackgroundColor(
                                ContextCompat.getColor(
                                    this@SettingsActivity, R.color.navy
                                )
                            )
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.show()
                    }.setCancelButton(getString(R.string.cancel)) {
                        it.dismissWithAnimation()
                    }
                sweetAlertDialog.show()
            }
        }
    }

    private fun setTheme(mode: Boolean, themeViewModel: ThemeViewModel) {
        if (mode) {
            themeViewModel.saveTheme(false)
            Toast.makeText(
                this@SettingsActivity, getString(R.string.light_mode), Toast.LENGTH_SHORT
            ).show()
        } else {
            themeViewModel.saveTheme(true)
            Toast.makeText(
                this@SettingsActivity, getString(R.string.night_mode), Toast.LENGTH_SHORT
            ).show()
        }
    }
}