package com.candra.dicodingstoryapplication.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.candra.dicodingstoryapplication.activity.main.MainViewModel
import com.candra.dicodingstoryapplication.data.di.Injection
import com.candra.dicodingstoryapplication.data.repository.MainRepository

class MainViewModelFactory(private val mainRepository: MainRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(mainRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MainViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, reload: Boolean): MainViewModelFactory {
            if (INSTANCE == null || reload) {
                synchronized(MainViewModelFactory::class.java) {
                    INSTANCE = MainViewModelFactory(Injection.mainProvideRepository(context))
                }
            }
            return INSTANCE as MainViewModelFactory
        }
    }
}