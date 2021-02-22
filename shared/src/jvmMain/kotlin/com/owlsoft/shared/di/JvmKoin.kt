package com.owlsoft.shared.di

import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.dsl.module

actual val logger = object : AppLogger() {
    override fun log(level: Level, msg: MESSAGE) {
        println("$level### $msg")
    }
}

actual val platformModule = module {
}