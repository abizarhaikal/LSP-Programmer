package com.example.lspprogrammer.core.di

import com.example.lspprogrammer.core.repository.IMyRepository
import com.example.lspprogrammer.core.repository.MyRepository
import com.example.lspprogrammer.pref.UserPreference
import com.example.lspprogrammer.pref.dataStore
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


val preferenceModule = module {
    single { UserPreference.getInstance(androidApplication().dataStore) }
}

val repositoryModule = module {
    single<IMyRepository> { MyRepository(get()) }
}