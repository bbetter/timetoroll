package com.owlsoft.backend.managers

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.coroutines.CoroutineContext

class AuthorizedWebSocketSession(
    val ownerID: String,
    private val webSocketSession: WebSocketSession
) {

    val commands = webSocketSession.incoming.receiveAsFlow()
        .filterIsInstance<Frame.Text>()

    suspend fun send(msg: String) {
        webSocketSession.send(Frame.Text(msg))
    }
}