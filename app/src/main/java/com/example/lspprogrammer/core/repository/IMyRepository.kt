package com.example.lspprogrammer.core.repository

import com.example.lspprogrammer.pref.UserModel
import kotlinx.coroutines.flow.Flow


interface IMyRepository {

    suspend fun saveUser(email : String, userId : String, name : String, isLogin : Boolean, role: String)

    fun getUserSession() : Flow<UserModel>

    suspend fun logout()
}