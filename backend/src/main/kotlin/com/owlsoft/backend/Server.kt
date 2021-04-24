package com.owlsoft.backend

import com.owlsoft.backend.data.LocalEncountersDataSource
import com.owlsoft.backend.managers.EncountersManager
import com.owlsoft.backend.routes.createEncounterRoute
import com.owlsoft.backend.routes.encounterByCodeRoute
import com.owlsoft.backend.routes.encounterTrackerRoute
import com.owlsoft.backend.routes.joinEncounterRoute
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
fun main() {

    val port = System.getenv("PORT")?.toInt() ?: 8080

    val embeddedServer = embeddedServer(Netty, port) {
        setupFeatures()

        val encountersManager = EncountersManager(LocalEncountersDataSource, log)

        routing {
            static("files") {
            }

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