package com.example.map.googlemap

import android.app.Application
import com.example.map.googlemap.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@CarApplication)
            modules(
                listOf(
                    networkModule,
                    dataSourceModel,
                    viewModelModule,
                    apiModule,
                    prefUtilsModule
                )
            )
        }
    }
}