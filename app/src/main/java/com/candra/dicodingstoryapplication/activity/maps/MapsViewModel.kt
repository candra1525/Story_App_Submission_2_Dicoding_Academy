package com.candra.dicodingstoryapplication.activity.maps

import androidx.lifecycle.ViewModel
import com.candra.dicodingstoryapplication.data.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    suspend fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}