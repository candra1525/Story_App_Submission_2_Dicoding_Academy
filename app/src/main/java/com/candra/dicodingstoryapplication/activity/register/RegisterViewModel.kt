package com.candra.dicodingstoryapplication.activity.register

import androidx.lifecycle.ViewModel
import com.candra.dicodingstoryapplication.data.repository.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getRegister(name: String, email: String, password: String) =
        storyRepository.getRegister(name, email, password)
}
