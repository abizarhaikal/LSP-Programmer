package com.example.lspprogrammer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.lspprogrammer.core.repository.usecaserepo.MyUseCase
import com.example.lspprogrammer.pref.UserModel

class OrderViewModel(private val repository: MyUseCase) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getUserSession().asLiveData()
    }
}