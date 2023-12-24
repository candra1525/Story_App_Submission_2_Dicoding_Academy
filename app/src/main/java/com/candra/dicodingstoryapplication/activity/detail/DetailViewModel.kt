package com.candra.dicodingstoryapplication.activity.detail

import androidx.lifecycle.ViewModel
import com.candra.dicodingstoryapplication.data.repository.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getDetail(id: String) = storyRepository.getDetail(id)
}