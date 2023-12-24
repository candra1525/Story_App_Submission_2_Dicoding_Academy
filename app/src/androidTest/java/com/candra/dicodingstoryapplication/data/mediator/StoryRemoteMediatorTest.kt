package com.candra.dicodingstoryapplication.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.candra.dicodingstoryapp.data.response.DetailStoryResponse
import com.candra.dicodingstoryapp.data.response.LoginResponse
import com.candra.dicodingstoryapp.data.response.NewStoryResponse
import com.candra.dicodingstoryapp.data.response.RegisterResponse
import com.candra.dicodingstoryapplication.data.api.ApiService
import com.candra.dicodingstoryapplication.data.database.StoryDatabase
import com.candra.dicodingstoryapplication.data.response.AllStoryResponse
import com.candra.dicodingstoryapplication.data.response.ListStoryItem
import com.candra.dicodingstoryapplication.model.StoryModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlocking {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, StoryModel>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}


class FakeApiService : ApiService {
    override suspend fun setRegister(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        TODO("Not yet implemented")
    }

    override suspend fun setLogin(email: String, password: String): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addNewStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ): NewStoryResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(page: Int, size: Int): AllStoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val listStory = ListStoryItem(
                i.toString(),
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR-ssnq8NfEhbE4XaEXIgUvq9ruJyYnIkLYLQ&usqp=CAU",
                "2023-10-10",
                "Testing",
                "Ini adalah testing Mediator",
                10.0,
                10.0
            )
            items.add(listStory)
        }
        return AllStoryResponse(items, false, null)
    }

    override suspend fun getStoriesWithLocation(location: Int): AllStoryResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getDetailStory(id: String): DetailStoryResponse {
        TODO("Not yet implemented")
    }

}