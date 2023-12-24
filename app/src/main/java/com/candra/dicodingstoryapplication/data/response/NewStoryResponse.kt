package com.candra.dicodingstoryapp.data.response

import com.google.gson.annotations.SerializedName

data class NewStoryResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
