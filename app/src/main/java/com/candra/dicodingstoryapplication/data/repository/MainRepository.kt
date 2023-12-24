package com.candra.dicodingstoryapplication.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.candra.dicodingstoryapplication.data.api.ApiService
import com.candra.dicodingstoryapplication.data.database.StoryDatabase
import com.candra.dicodingstoryapplication.data.mediator.StoryRemoteMediator
import com.candra.dicodingstoryapplication.data.preferences.TokenPreferences
import com.candra.dicodingstoryapplication.model.StoryModel

class MainRepository constructor(
    private val apiService: ApiService,
    private val pref: TokenPreferences,
    private val storyDatabase: StoryDatabase
) {
    fun getSessionToken() = pref.getSessionToken()

    @OptIn(ExperimentalPagingApi::class)
    fun getStory(): LiveData<PagingData<StoryModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}