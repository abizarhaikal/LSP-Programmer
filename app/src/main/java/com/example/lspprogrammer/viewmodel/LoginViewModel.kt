package com.example.lspprogrammer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.lspprogrammer.core.repository.usecaserepo.MyUseCase
import com.example.lspprogrammer.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel (private val useCase: MyUseCase) : ViewModel(){

    fun saveUser(email: String, userId: String, name: String, isLogin: Boolean, role:String) {
        viewModelScope.launch {
            useCase.saveUser(email, userId, name, isLogin, role)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return useCase.getUserSession().asLiveData()
    }


}