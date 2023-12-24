package com.candra.dicodingstoryapplication.activity.login

import androidx.lifecycle.ViewModel
import com.candra.dicodingstoryapplication.data.repository.StoryRepository
import com.candra.dicodingstoryapplication.model.UserModel

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getLogin(email: String, password: String) = storyRepository.getLogin(email, password)
    suspend fun setSessionToken(user: UserModel) = storyRepository.setSessionToken(user)
}