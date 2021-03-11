package com.owlsoft.shared.di

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.remote.EncounterAPI
import com.owlsoft.shared.remote.RemoteEncounterTracker
import com.owlsoft.shared.usecases.CreateEncounterUseCase
import com.owlsoft.shared.usecases.GetEncounterUseCase
import com.owlsoft.shared.usecases.JoinEncounterUseCase
import com.owlsoft.shared.usecases.UpdateEncounterUseCase
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

typealias AppLogger = org.koin.core.logger.Logger

fun initKoin(koinAppDeclaration: KoinAppDeclaration = {}) = startKoin {
    koinAppDeclaration()
    modules(platformModule, coreModule)
}

private val coreModule = module {
    single { logger }

    single { createHttpClient() }

    single { EncounterAPI(get()) }

    single { UUIDRepository(get(), get()) }

    factory { params ->
        val (_, code) = params.component1<Pair<String, String>>()
        RemoteEncounterTracker(
            code,
            get()
        )
    }

    useCases()
}

private fun createHttpClient() = HttpClient {
    install(JsonFeature) {
        val json = kotlinx.serialization.json.Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
        serializer = KotlinxSerializer(json)
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}

private fun Module.useCases() {
    single { CreateEncounterUseCase(get(), get()) }
    single { JoinEncounterUseCase(get(), get()) }
    single { GetEncounterUseCase(get()) }
    single { UpdateEncounterUseCase(get()) }
}

expect val logger: AppLogger
expect val platformModule: Module
