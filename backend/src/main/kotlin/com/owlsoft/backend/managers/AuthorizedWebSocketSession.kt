package com.owlsoft.backend.managers

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance

class AuthorizedWebSocketSession(
    val deviceID: String,
    private val webSocketSession: WebSocketSession
) {

    val commands = webSocketSession.incoming.consumeAsFlow()
        .filterIsInstance<Frame.Text>()

    suspend fun send(msg: String) {
        webSocketSession.send(Frame.Text(msg))
    }

    override fun toString(): String {
        return "Session#${webSocketSession.hashCode()}[auth by $deviceID]"
    }
}