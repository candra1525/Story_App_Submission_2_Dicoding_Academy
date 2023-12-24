package com.candra.dicodingstoryapplication.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.candra.dicodingstoryapp.data.response.LoginResponse
import com.candra.dicodingstoryapp.data.response.NewStoryResponse
import com.candra.dicodingstoryapp.data.response.RegisterResponse
import com.candra.dicodingstoryapp.data.response.Story
import com.candra.dicodingstoryapplication.data.api.ApiService
import com.candra.dicodingstoryapplication.data.preferences.TokenPreferences
import com.candra.dicodingstoryapplication.data.response.ListStoryItem
import com.candra.dicodingstoryapplication.data.result.Result
import com.candra.dicodingstoryapplication.model.UserModel
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository constructor(
    private val apiService: ApiService,
    private val pref: TokenPreferences
) {
    // Preferences
    suspend fun setSessionToken(user: UserModel) = pref.setSessionToken(user)

    suspend fun logout() {
        pref.logout()
    }

    // Login
    fun getLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.setLogin(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    // Register
    fun getRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.setRegister(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    suspend fun getStoriesWithLocation(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            if (response.error == false) {
                val listStory = response.listStory?.filterNotNull() ?: emptyList()
                emit(Result.Success(listStory))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Detail Story
    fun getDetail(id: String): LiveData<Result<Story>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStory(id)
            if (response.error == false) {
                response.story?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Story data is null"))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Send Story to API
    suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ): LiveData<Result<NewStoryResponse>> = liveData {
        try {
            val response = apiService.addNewStory(file, description, lat, lon)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}