package com.example.lspprogrammer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lspprogrammer.core.repository.usecaserepo.MyUseCase
import kotlinx.coroutines.launch

class ReportViewModel(private val repository: MyUseCase) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}