package com.candra.dicodingstoryapplication.model

data class UserModel(
    val token: String,
    val name: String,
    val isUserLogin: Boolean = false
)
