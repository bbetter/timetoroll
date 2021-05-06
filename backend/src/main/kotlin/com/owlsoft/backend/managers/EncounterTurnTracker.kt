package com.owlsoft.backend.managers


import com.owlsoft.shared.model.Encounter
import com.owlsoft.shared.model.TickData
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.CopyOnWriteArrayList

class EncounterTurnTracker(
    private val turnTracker: TurnTracker,
    private var encounter: Encounter,
    private val sessions: CopyOnWriteArrayList<AuthorizedWebSocketSession> = CopyOnWriteArrayList(),
) {

    suspend fun track() {
        turnTracker.track()
            .collectLatest {
                for (session in sessions) {
                    session.sendTrackerData(it)
                }
            }
    }

    fun pause() {
        turnTracker.pause()
    }

    suspend fun newSession(
        session: AuthorizedWebSocketSession,
        onLastSessionCompleted: () -> Unit
    ) {
        sessions.add(session)

        session.commands
            .collect { frame ->
                try {
                    when (frame.readText()) {
                        "skip" -> skipTurn(session.deviceID)
                        "resume" -> resume(session.deviceID)
                        "pause" -> pause(session.deviceID)
                    }
                } catch (exception: Exception) {

                }
            }

        sessions.remove(session)

        if (sessions.isEmpty()) {
            onLastSessionCompleted()
        }
    }

    fun updateEncounter(encounter: Encounter) {
        this.encounter = encounter
        turnTracker.updateTurnCount(encounter.participants.size)
    }

    private suspend fun AuthorizedWebSocketSession.sendTrackerData(
        trackerData: TrackerData
    ) {
        val participants = encounter.participants

        if (participants.isEmpty()) {
            return
        }

        val isAdminSession = deviceID == encounter.ownerID

        val isCurrentSession =
            participants[trackerData.turnIndex].ownerID == deviceID

        val data = TickData(
            trackerData.timerTick,
            trackerData.turnIndex,
            trackerData.roundIndex,
            trackerData.isPaused,
            isAdminSession,
            isAdminSession || isCurrentSession,
            participants
        )

        val text = Json.encodeToString(data)

        try {
            send(text)
        } catch (ex: Exception) {

        }
    }


    private fun skipTurn(auth: String) {
        val turn = turnTracker.state.turnIndex

        val currentCharacter = encounter.participants[turn]

        if (currentCharacter.ownerID == auth || auth == encounter.ownerID) {
            turnTracker.nextTurn()
        }
    }

    private fun pause(auth: String) {
        if (auth == encounter.ownerID) {
            turnTracker.pause()
        }
    }

    private fun resume(auth: String) {
        if (auth == encounter.ownerID) {
            turnTracker.resume()
        }
    }

    fun cancel() {
        turnTracker.cancel()
    }
}