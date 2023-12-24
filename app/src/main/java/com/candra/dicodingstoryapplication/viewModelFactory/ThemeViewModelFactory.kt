package com.candra.dicodingstoryapplication.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.candra.dicodingstoryapplication.data.preferences.ThemePreferences
import com.candra.dicodingstoryapplication.themeViewModel.ThemeViewModel

class ThemeViewModelFactory(private val pref: ThemePreferences) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class : ${modelClass.name}")
    }
}