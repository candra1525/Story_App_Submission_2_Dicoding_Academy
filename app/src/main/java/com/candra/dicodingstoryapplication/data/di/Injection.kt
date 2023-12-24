package com.candra.dicodingstoryapplication.data.di

import android.content.Context
import com.candra.dicodingstoryapplication.data.api.ApiConfig
import com.candra.dicodingstoryapplication.data.database.StoryDatabase
import com.candra.dicodingstoryapplication.data.preferences.TokenPreferences
import com.candra.dicodingstoryapplication.data.preferences.dataStore
import com.candra.dicodingstoryapplication.data.repository.MainRepository
import com.candra.dicodingstoryapplication.data.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = TokenPreferences.getInstance(context.dataStore)
        val user = runBlocking {
            pref.getSessionToken().first()
        }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository(apiService, pref)
    }

    fun mainProvideRepository(context: Context): MainRepository {
        val pref = TokenPreferences.getInstance(context.dataStore)
        val user = runBlocking {
            pref.getSessionToken().first()
        }
        val db = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(user.token)
        return MainRepository(apiService, pref, db)
    }
}