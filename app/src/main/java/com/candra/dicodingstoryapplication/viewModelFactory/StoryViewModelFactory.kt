package com.candra.dicodingstoryapplication.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.candra.dicodingstoryapplication.activity.addstory.AddStoryViewModel
import com.candra.dicodingstoryapplication.activity.detail.DetailViewModel
import com.candra.dicodingstoryapplication.activity.login.LoginViewModel
import com.candra.dicodingstoryapplication.activity.maps.MapsViewModel
import com.candra.dicodingstoryapplication.activity.register.RegisterViewModel
import com.candra.dicodingstoryapplication.activity.settings.SettingsViewModel
import com.candra.dicodingstoryapplication.data.di.Injection
import com.candra.dicodingstoryapplication.data.repository.StoryRepository

class StoryViewModelFactory(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, reload: Boolean): StoryViewModelFactory {
            if (INSTANCE == null || reload) {
                synchronized(StoryViewModelFactory::class.java) {
                    INSTANCE = StoryViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as StoryViewModelFactory
        }
    }
}