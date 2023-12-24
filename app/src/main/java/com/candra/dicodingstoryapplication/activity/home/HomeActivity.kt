package com.candra.dicodingstoryapplication.activity.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.activity.login.LoginActivity
import com.candra.dicodingstoryapplication.databinding.ActivityHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            ivGetStarted.setImageResource(R.drawable.get_started)
            fabGetStarted.setOnClickListener {
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        runBlocking {
            launch {
                delay(3000L)
                playAnimation()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivGetStarted, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val hello = getAlpha(binding.tvHello, 1000)
        val welcome = getAlpha(binding.tvWelcome, 1000)
        val desc = getAlpha(binding.tvDesc, 1000)
        val getStarted = getAlpha(binding.fabGetStarted, 1000)

        AnimatorSet().apply {
            playSequentially(hello, welcome, desc, getStarted)
            start()
        }
    }

    private fun getAlpha(view: View, duration: Long): ObjectAnimator =
        ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(duration)
}