package com.candra.dicodingstoryapplication.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.activity.addstory.AddStoryActivity
import com.candra.dicodingstoryapplication.activity.maps.MapsActivity
import com.candra.dicodingstoryapplication.activity.settings.SettingsActivity
import com.candra.dicodingstoryapplication.activity.splashscreen.SplashScreenActivity
import com.candra.dicodingstoryapplication.data.adapter.LoadingStateAdapter
import com.candra.dicodingstoryapplication.data.adapter.StoryAdapter
import com.candra.dicodingstoryapplication.databinding.ActivityMainBinding
import com.candra.dicodingstoryapplication.viewModelFactory.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModelFactory.getInstance(this@MainActivity, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getInit()
    }

    private fun getInit() {
        showLoading(true)
        getDataWithConnection()
        binding.rvListStory.visibility = View.VISIBLE
        setRvLayoutAndItemDecoration()
        getPreferences()
        refresh()

        binding.apply {
            settings.setOnClickListener {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
            fabAddStory.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }
            maps.setOnClickListener {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getPreferences() {
        mainViewModel.getSessionToken().observe(this) {
            if (!it.isUserLogin) {
                startActivity(Intent(this, SplashScreenActivity::class.java))
                finish()
            } else {
                binding.tvNameUser.text = getString(R.string.hellouser, it.name)
            }
        }
    }

    private fun getDataStory() {
        showLoading(false)
        val adapter = StoryAdapter()
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }
        binding.swipeToRefresh.isRefreshing = false
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoadingMain.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showOffline(isOffline: Boolean) {
        binding.apply {
            rvListStory.visibility = if (isOffline) View.GONE else View.VISIBLE
            llOffline.visibility = if (isOffline) View.VISIBLE else View.GONE
        }
    }

    private fun getDataWithConnection() {
        mainViewModel.checkConnection(this)
        mainViewModel.checkConnection.observe(this) { connect: Boolean ->
            if (connect) {
                showOffline(false)
                getDataStory()
            } else {
                showOffline(true)
            }
        }
        binding.swipeToRefresh.isRefreshing = false
    }

    private fun refresh() {
        binding.swipeToRefresh.apply {
            setOnRefreshListener {
                getDataWithConnection()
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.data_update),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setRvLayoutAndItemDecoration() {
        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.apply {
            rvListStory.layoutManager = layoutManager
            rvListStory.addItemDecoration(itemDecoration)
            rvListStory.setHasFixedSize(true)
        }
    }
}