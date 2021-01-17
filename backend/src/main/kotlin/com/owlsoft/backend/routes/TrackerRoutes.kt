package com.owlsoft.backend.routes

import com.owlsoft.backend.managers.TrackersManager

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.InternalCoroutinesApi

const val trackerPath = "/tracker"

@OptIn(InternalCoroutinesApi::class)
fun Route.trackRoute() {
    webSocket("$trackerPath/{code}") {
        val code = call.parameters["code"] ?: kotlin.run {
            call.respond(HttpStatusCode.NoContent)
            return@webSocket
        }

        TrackersManager.join(code, this)
        for (frame in incoming) {
            if(frame !is Frame.Text) continue

            if (frame.readText() == "skip") {
                TrackersManager.skipTurn(code)
            }
        }
    }
}