package com.example.lspprogrammer

import android.app.Application
import com.example.lspprogrammer.core.di.preferenceModule
import com.example.lspprogrammer.core.di.repositoryModule
import com.example.lspprogrammer.core.di.useCaseModule
import com.example.lspprogrammer.core.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    preferenceModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}