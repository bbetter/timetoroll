package com.owlsoft.shared.sockets

import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import kotlin.native.concurrent.ThreadLocal


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