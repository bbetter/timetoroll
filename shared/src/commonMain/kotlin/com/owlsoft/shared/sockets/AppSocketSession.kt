package com.owlsoft.shared.sockets

import io.ktor.utils.io.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class AppSocketSession private constructor(
    private val socket: AppSocket
) : Closeable {

    companion object {
        fun create(auth: String, url: String) = AppSocketSession(AppSocket(auth, url))
    }

    val incoming = callbackFlow {
        socket.connect()
        socket.messageListener = {
            offer(it)
        }
        awaitClose { socket.disconnect() }
    }

    fun send(msg: String) {
        socket.send(msg)
    }

    override fun close() {
        socket.disconnect()
    }
}