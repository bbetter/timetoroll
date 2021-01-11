package com.owlsoft.shared.di

import org.koin.core.module.Module

expect object PlatformModuleProvider {
    fun provide(): Module
}