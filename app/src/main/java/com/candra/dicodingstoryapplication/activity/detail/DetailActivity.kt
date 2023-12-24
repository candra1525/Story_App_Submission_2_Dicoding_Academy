package com.candra.dicodingstoryapplication.activity.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.data.result.Result
import com.candra.dicodingstoryapplication.databinding.ActivityDetailBinding
import com.candra.dicodingstoryapplication.viewModelFactory.StoryViewModelFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        StoryViewModelFactory.getInstance(this@DetailActivity, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val varId: String = intent.getStringExtra(id).toString()
        getDetailStory(varId)

        binding.backToMainFromDetail.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getDetailStory(id: String) {
        detailViewModel.getDetail(id).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val dataDetail = result.data
                        binding.apply {
                            tvDetailName.text = dataDetail.name
                            ivDetailPhoto.loadImage(dataDetail.photoUrl.toString())
                            tvDetailDescription.text = dataDetail.description.toString()
                            tvDetailCreatedAt.text =
                                dataDetail.createdAt?.substring(0, 9)?.withDateFormat()
                            tvDetailLatLong.text =
                                if (dataDetail.lat != null && dataDetail.lon != null) "${dataDetail.lat}, ${dataDetail.lon}" else getString(
                                    R.string.location_unknown
                                )
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        Log.e(TAG, "Error : ${result.error}")
                        Toast.makeText(
                            this,
                            getString(R.string.problem_occur),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(this.context).load(url).placeholder(R.drawable.image_broken).into(this)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoadingDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun String.withDateFormat(): String {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val date = format.parse(this) as Date
        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }

    companion object {
        private const val id: String = "ID"
        private const val TAG = "Detail Activity"
    }
}