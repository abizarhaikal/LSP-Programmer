package com.example.lspprogrammer.pref

data class UserModel (
    val email : String,
    val userId: String,
    val name: String,
    val isLogin : Boolean = false,
    val role : String
)