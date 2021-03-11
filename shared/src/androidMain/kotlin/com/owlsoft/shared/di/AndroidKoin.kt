package com.owlsoft.shared.di

import android.content.Context
import android.util.Log
import com.owlsoft.shared.Storage
import com.owlsoft.shared.StorageImpl
import com.owlsoft.shared.UUIDGenerator
import com.owlsoft.shared.UUIDGeneratorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.dsl.module

const val PREFERENCES_KEY = "default"

actual val platformModule = module {
    single { androidContext().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE) }

    single<Storage> { StorageImpl(get()) }
    single<UUIDGenerator> { UUIDGeneratorImpl() }

}

actual val logger = object : Logger() {
    override fun log(level: Level, msg: MESSAGE) {
        Log.d("$level", msg)
    }
}
