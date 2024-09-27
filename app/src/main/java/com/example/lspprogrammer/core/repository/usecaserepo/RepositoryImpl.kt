package com.example.lspprogrammer.core.repository.usecaserepo

import com.example.lspprogrammer.core.repository.IMyRepository
import com.example.lspprogrammer.pref.UserModel
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(private val myRepository: IMyRepository) : MyUseCase {
    override suspend fun saveUser(email: String, userId: String, name: String, isLogin: Boolean, role: String) {
        myRepository.saveUser(email, userId, name, isLogin, role)
    }

    override fun getUserSession(): Flow<UserModel> {
        return myRepository.getUserSession()
    }

    override suspend fun logout() {
        myRepository.logout()
    }

}