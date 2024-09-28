package com.example.lspprogrammer.core.di

import com.example.lspprogrammer.core.repository.usecaserepo.MyUseCase
import com.example.lspprogrammer.core.repository.usecaserepo.RepositoryImpl
import com.example.lspprogrammer.viewmodel.LoginViewModel
import com.example.lspprogrammer.viewmodel.OrderViewModel
import com.example.lspprogrammer.viewmodel.PaymentViewModel
import com.example.lspprogrammer.viewmodel.ProfileViewModel
import com.example.lspprogrammer.viewmodel.ReportViewModel
import com.example.lspprogrammer.viewmodel.StrukViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<MyUseCase> { RepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { OrderViewModel(get()) }
    viewModel { StrukViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { ReportViewModel(get()) }
}