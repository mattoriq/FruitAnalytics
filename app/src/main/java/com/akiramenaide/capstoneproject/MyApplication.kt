package com.akiramenaide.capstoneproject

import android.app.Application
import com.akiramenaide.capstoneproject.core.di.databaseModule
import com.akiramenaide.capstoneproject.core.di.repositoryModule
import com.akiramenaide.capstoneproject.ui.di.useCaseModule
import com.akiramenaide.capstoneproject.ui.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    databaseModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}