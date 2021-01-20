package com.owlsoft.shared.sockets

import io.ktor.utils.io.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel

@ExperimentalCoroutinesApi
class AppSocketSession private constructor(
    private val socket: AppSocket
) : Closeable {

    companion object {
        fun create(auth: String, url: String) = AppSocketSession(AppSocket(auth, url))
    }


    val incoming = BroadcastChannel<String>(1).apply {
        socket.connect()
        socket.messageListener = {
            offer(it)
        }
    }

    fun send(msg: String) {
        socket.send(msg)
    }

    override fun close() {
        socket.disconnect()
    }

}