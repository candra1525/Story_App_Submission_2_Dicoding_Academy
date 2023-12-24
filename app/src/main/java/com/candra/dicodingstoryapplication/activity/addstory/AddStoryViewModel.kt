package com.candra.dicodingstoryapplication.activity.addstory

import androidx.lifecycle.ViewModel
import com.candra.dicodingstoryapplication.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ) = storyRepository.uploadStory(file, description, lat, lon)
}