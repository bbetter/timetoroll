package com.owlsoft.shared.remote

import com.owlsoft.shared.sockets.AppSocket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class, FlowPreview::class)
class TrackerAPI(
    val encounterCode: String
) {
    companion object {
        private const val socketUrl = "ws://10.0.2.2:8080"
    }

    init {

    }

    private val channel = BroadcastChannel<String>(1)

    private val socket = AppSocket("$socketUrl/tracker/$encounterCode")
        .apply {
            messageListener = {
                channel.offer(it)
            }
        }

    fun listenToEvents(): Flow<String> {
        return channel.asFlow()
            .onStart {
                if (socket.currentState != AppSocket.State.CONNECTED) {
                    socket.connect()
                }
            }
    }

    fun skipTurn() {
        if (socket.currentState != AppSocket.State.CONNECTED) {
            socket.connect()
        }
        socket.send("skip")
    }

    private fun send(msg: String) {
        socket.send(msg)
    }
}