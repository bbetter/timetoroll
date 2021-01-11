package com.owlsoft.shared.di

import com.owlsoft.shared.Storage
import com.owlsoft.shared.StorageImpl
import com.owlsoft.shared.UUIDGenerator
import com.owlsoft.shared.UUIDGeneratorImpl
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual object PlatformModuleProvider {
    actual fun provide() = module {
        single { NSUserDefaults.standardUserDefaults }
        single<Storage> { StorageImpl(get()) }
        single<UUIDGenerator> { UUIDGeneratorImpl() }
    }
}