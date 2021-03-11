package com.owlsoft.shared.sockets

import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


class AppSocketSession private constructor(
    private val socket: AppSocket,
) : Closeable {

    companion object {
        fun create(auth: String, url: String) =
            AppSocketSession(AppSocket(auth, url))
    }

    val incoming = callbackFlow {
        socket.messageListener = {
            offer(it)
        }
        awaitClose { socket.disconnect() }
    }

    val states = callbackFlow {
        socket.stateListener = {
            offer(it.name)
        }
        awaitClose { socket.disconnect() }
    }

    fun connect() {
        socket.connect()
    }

    fun send(msg: String) {
        socket.send(msg)
    }

    override fun close() {
        socket.disconnect()
    }
}