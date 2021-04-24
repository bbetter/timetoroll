package com.owlsoft.backend

import com.owlsoft.backend.data.LocalEncountersDataSource
import com.owlsoft.backend.managers.EncountersManager
import com.owlsoft.backend.routes.createEncounterRoute
import com.owlsoft.backend.routes.encounterByCodeRoute
import com.owlsoft.backend.routes.encounterTrackerRoute
import com.owlsoft.backend.routes.joinEncounterRoute
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import java.io.File

@OptIn(InternalCoroutinesApi::class)
fun main() {

    val port = System.getenv("PORT")?.toInt() ?: 8080

    val embeddedServer = embeddedServer(Netty, port) {
        setupFeatures()

        val encountersManager = EncountersManager(LocalEncountersDataSource, log)

        routing {
            get("/") {
                call.respondText(ContentType.Text.Html) {
                    getHtml()
                }
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

@Language("HTML")
private fun getHtml() = """
    <html>
        <head>
            <title>Time 2 Roll</title>
        </head>
        <body>
            <h1>Time 2 Roll is coming... </h1>
            <h2><i>Stay Tuned...</i></h2>
        </body>
    </html>
""".trimIndent()

private fun Application.setupFeatures() {
    install(ContentNegotiation) {
        json()
    }

    install(WebSockets)
    install(CallLogging)
}