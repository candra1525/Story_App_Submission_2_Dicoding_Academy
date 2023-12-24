package com.candra.dicodingstoryapplication.data.api

import com.candra.dicodingstoryapp.data.response.*
import com.candra.dicodingstoryapplication.data.response.AllStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // Register
    @FormUrlEncoded
    @POST("register")
    suspend fun setRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // Login
    @FormUrlEncoded
    @POST("login")
    suspend fun setLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // Add New Story
    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): NewStoryResponse

    // Get ALl Stroies
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): AllStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1,
    ): AllStoryResponse

    // Detail
    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): DetailStoryResponse

}