package com.candra.dicodingstoryapplication.activity.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.candra.dicodingstoryapplication.data.repository.StoryRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }
}