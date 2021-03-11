package com.owlsoft.backend

import com.owlsoft.backend.data.*
import com.owlsoft.backend.managers.EncountersManager
import com.owlsoft.backend.routes.*

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
fun main() {

    val embeddedServer = embeddedServer(Netty, 8080) {
        setupFeatures()

        val encountersManager = EncountersManager(LocalEncountersDataSource, log)

        routing {
            encounterByCodeRoute(LocalEncountersDataSource)
            createEncounterRoute(encountersManager)
            joinEncounterRoute(encountersManager)

            encounterTrackerRoute(encountersManager)

            launch {
                encountersManager.listenToTrackerCreation()
            }
        }

    }
    embeddedServer.start(wait = true)
}

private fun Application.setupFeatures() {
    install(ContentNegotiation) {
        json()
    }

    install(WebSockets)
    install(CallLogging)
}