package com.example.lspprogrammer.core.repository.usecaserepo

import com.example.lspprogrammer.pref.UserModel
import kotlinx.coroutines.flow.Flow

interface MyUseCase {
    suspend fun saveUser(email: String, userId: String, name: String, isLogin: Boolean, role:String)

    fun getUserSession(): Flow<UserModel>

    suspend fun logout()
}