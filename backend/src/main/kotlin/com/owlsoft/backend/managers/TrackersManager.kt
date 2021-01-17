package com.owlsoft.backend.managers

import com.owlsoft.backend.data.Encounter
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap

object TrackersManager {

    private val trackers: ConcurrentHashMap<String, EncounterTurnTracker> = ConcurrentHashMap(6)

    val trackersChannel = Channel<Collection<EncounterTurnTracker>>()

    fun newTracker(encounter: Encounter) {
        trackers[encounter.code] = EncounterTurnTracker(
            encounter,
            emptyList()
        )
        trackersChannel.offer(trackers.values)
    }

    suspend fun skipTurn(code: String) {
        trackers[code]?.skipTurn()
    }

    suspend fun join(code: String, webSocketSession: WebSocketSession) {
        trackers[code]?.addSession(webSocketSession)
    }

    suspend fun listenToTrackers(application: Application) {
        for (event in trackersChannel) {
            application.log.debug("### TRACKERS $event")
            event.forEach {
                it.broadcast(application).collectLatest { }
            }
        }
    }

    suspend fun updateTrackerConfig(code: String, newEncounter: Encounter) {
        trackers[code]?.updateConfig(
            newEncounter
        )
    }
}