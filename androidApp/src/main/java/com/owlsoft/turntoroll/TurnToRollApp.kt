package com.owlsoft.turntoroll

import android.app.Application
import com.owlsoft.turntoroll.shared.initKoin

class TurnToRollApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}