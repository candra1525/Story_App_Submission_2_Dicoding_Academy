package com.candra.dicodingstoryapplication.data.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.activity.detail.DetailActivity
import com.candra.dicodingstoryapplication.databinding.ListStoryBinding
import com.candra.dicodingstoryapplication.model.StoryModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoryAdapter : PagingDataAdapter<StoryModel, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
        holder.itemView.setOnClickListener()
        {
            val moveDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            if (story != null) {
                moveDetail.putExtra("ID", story.id)
            }
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.binding.ivItemPhoto, "photo"),
                    Pair(holder.binding.tvItemName, "name"),
                    Pair(holder.binding.tvItemDescription, "description"),
                    Pair(holder.binding.tvItemLatLong, "latlong")
                )
            holder.itemView.context.startActivity(moveDetail, optionsCompat.toBundle())
        }
    }

    inner class ViewHolder(val binding: ListStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryModel) {
            binding.apply {
                ivItemPhoto.loadImage(story.photoUrl.toString())
                tvItemName.text = story.name
                tvItemDescription.text = story.description
                tvItemCreatedAt.text = story.createdAt?.substring(0, 9)?.withDateFormat()
                if (story.lat != null && story.lon != null) {
                    tvItemLatLong.visibility = View.VISIBLE
                    tvItemLatLong.text = "${story.lat.toString()}, ${story.lon.toString()}"
                } else {
                    tvItemLatLong.visibility = View.GONE
                }
            }
        }
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(this.context).load(url).placeholder(R.drawable.image_broken).into(this)
    }

    private fun String.withDateFormat(): String {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val date = format.parse(this) as Date
        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(
                oldItem: StoryModel,
                newItem: StoryModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryModel,
                newItem: StoryModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}