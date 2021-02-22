package com.owlsoft.shared.di

import com.owlsoft.shared.*
import com.owlsoft.shared.remote.EncounterAPI
import com.owlsoft.shared.usecases.CreateEncounterUseCase
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

import org.koin.dsl.module
import platform.Foundation.NSLog
import platform.Foundation.NSUserDefaults

fun init() = initKoin {
    logger(logger)
}

fun KoinApplication.createEncounterUseCase(): CreateEncounterUseCase {
    val encounterAPI = koin.get<EncounterAPI>()
    val uuidRepository = koin.get<UUIDRepository>()
    return CreateEncounterUseCase(encounterAPI, uuidRepository)
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