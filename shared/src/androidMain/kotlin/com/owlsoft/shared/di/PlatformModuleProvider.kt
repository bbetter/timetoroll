package com.owlsoft.shared.di

import android.content.Context
import com.owlsoft.shared.Storage
import com.owlsoft.shared.StorageImpl
import com.owlsoft.shared.UUIDGenerator
import com.owlsoft.shared.UUIDGeneratorImpl
import io.ktor.client.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual object PlatformModuleProvider {
    const val PREFERENCES_KEY = "default"

    actual fun provide(): Module = module {
        single { androidContext().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE) }
        single<Storage> { StorageImpl(get()) }
        single<UUIDGenerator> { UUIDGeneratorImpl() }
        single {

        }
    }

}