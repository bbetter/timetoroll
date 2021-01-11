package com.owlsoft.turntoroll

import android.app.Application
import com.owlsoft.shared.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TurnToRollApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@TurnToRollApp)
            modules(appModule)
        }
    }
}