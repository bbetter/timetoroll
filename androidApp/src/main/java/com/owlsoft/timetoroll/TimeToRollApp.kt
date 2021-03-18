package com.owlsoft.timetoroll

import android.app.Application
import com.owlsoft.shared.di.initKoin
import org.koin.android.ext.koin.androidContext

class TimeToRollApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TimeToRollApp)
            modules(appModule)
        }
    }
}