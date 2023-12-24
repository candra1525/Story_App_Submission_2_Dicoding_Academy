package com.candra.dicodingstoryapplication.activity.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.databinding.ActivityAboutDevBinding

class AboutDevActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutDevBinding
    private val linkedIn: String = "https://www.linkedin.com/in/candra1525/"
    private val instagram: String = "https://www.instagram.com/candracandra1525/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutDevBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            ivProfileDeveloper.setImageResource(R.drawable.developer_profile)
            backToMainFromAboutDev.setOnClickListener {
                onBackPressed()
            }
            tvLinkedInDeveloper.setOnClickListener {
                val openLinkedIn = Intent(Intent.ACTION_VIEW, Uri.parse(linkedIn))
                startActivity(openLinkedIn)
            }

            tvInstagramDeveloper.setOnClickListener {
                val openLinkedIn = Intent(Intent.ACTION_VIEW, Uri.parse(instagram))
                startActivity(openLinkedIn)
            }
        }

    }
}