package com.example.lspprogrammer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.lspprogrammer.core.repository.usecaserepo.MyUseCase
import com.example.lspprogrammer.pref.UserModel
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: MyUseCase) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getUserSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}