package com.owlsoft.backend.managers

import com.owlsoft.backend.data.Encounter
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

class EncounterTurnTracker(
    encounter: Encounter,
    private var currentSessions: List<WebSocketSession>
) {

    private val sessionsFlow = MutableStateFlow(currentSessions)

    private val tracker = TurnTracker(
        encounter.participants.size,
        encounter.singleTurnSeconds
    )

    fun broadcast(application: Application) =
        combine(tracker.observeResults(), sessionsFlow) { trackerData, sessions ->

            application.log.debug("### tracker data = $trackerData")
            application.log.debug("### sessions = $sessions")
            for (session in sessions) {
                val text = Json.encodeToString(trackerData)
                try {
                    session.send(Frame.Text(text))
                } catch (ex: Exception) {
                    removeSession(session)
                }
            }
        }

    suspend fun addSession(newSession: WebSocketSession) {
        currentSessions += newSession
        sessionsFlow.emit(currentSessions)
    }

    suspend fun removeSession(session: WebSocketSession) {
        currentSessions -= session
        sessionsFlow.emit(currentSessions)
    }

    suspend fun updateConfig(newEncounter: Encounter) {
        tracker.updateConfig(
            newEncounter.participants.size,
            newEncounter.singleTurnSeconds
        )
    }

    suspend fun skipTurn() {
        tracker.nextTurn()
    }
}