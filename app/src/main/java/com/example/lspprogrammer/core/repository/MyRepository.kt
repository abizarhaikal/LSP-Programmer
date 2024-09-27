package com.example.lspprogrammer.core.repository

import com.example.lspprogrammer.pref.UserModel
import com.example.lspprogrammer.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class MyRepository(private val userPreference: UserPreference) : IMyRepository {
    override suspend fun saveUser(email: String, userId: String, name: String, isLogin: Boolean, role:String) {
        userPreference.saveSession(UserModel(email, userId, name, isLogin, role))
    }

    override fun getUserSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    override suspend fun logout() {
        userPreference.logout()
    }

}