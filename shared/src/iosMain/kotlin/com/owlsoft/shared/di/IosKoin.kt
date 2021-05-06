package com.owlsoft.shared.di

import com.owlsoft.shared.Storage
import com.owlsoft.shared.StorageImpl
import com.owlsoft.shared.UUIDGenerator
import com.owlsoft.shared.UUIDGeneratorImpl
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import org.koin.dsl.module
import platform.Foundation.NSLog
import platform.Foundation.NSUserDefaults

fun init() = initKoin {
}

actual val platformModule = module {

    val defaults = NSUserDefaults(suiteName = "TTR")

    single<Storage> { StorageImpl(defaults) }
    single<UUIDGenerator> { UUIDGeneratorImpl() }
}

actual val logger = object : AppLogger() {
    override fun log(level: Level, msg: MESSAGE) {
        NSLog("${level.name}# %s", msg)
    }
}