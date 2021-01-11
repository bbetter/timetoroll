package com.owlsoft.shared.remote

import com.owlsoft.shared.sockets.AppSocket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class, FlowPreview::class)
class EncounterTurnAPI {
    companion object {
        private const val baseUrl = "ws://10.0.2.2:8080"
    }

    fun observeTurn(code: String): Flow<String>{
        val socket = AppSocket("$baseUrl/encounters/$code")
        socket.connect()
        return callbackFlow {
            socket.messageListener = {
                offer(it)
            }
            while (true) {
                awaitClose { close() }
            }
        }
            .catch { socket.disconnect() }
    }
}