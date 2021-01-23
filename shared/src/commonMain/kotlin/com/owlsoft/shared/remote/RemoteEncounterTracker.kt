package com.owlsoft.shared.remote

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.sockets.AppSocketSession
import io.ktor.utils.io.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class RemoteEncounterTracker(
    encounterCode: String,
    uuidRepository: UUIDRepository
) {
    companion object {
        //        private const val socketUrl = "ws://10.0.2.2:8080"
        private const val socketUrl = "ws://turntoroll.pagekite.me"
    }

    private val session = AppSocketSession.create(
        uuidRepository.getUUID(),
        "$socketUrl/tracker/$encounterCode"
    )

    fun messages() = session.incoming

    fun skipTurn() = session.send("skip")

    fun pause() = session.send("pause")

    fun resume() = session.send("resume")
}