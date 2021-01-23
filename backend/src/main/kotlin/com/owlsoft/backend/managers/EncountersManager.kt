package com.owlsoft.backend.managers

import com.owlsoft.backend.data.Encounter
import com.owlsoft.backend.data.EncountersDataSource
import com.owlsoft.backend.data.TrackerData
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class EncountersManager(
    private val encountersDataSource: EncountersDataSource
) {

    private val _trackersUpdateChannel = BroadcastChannel<String>(1)
    private val _sessionsUpdateChannel = BroadcastChannel<String>(1)

    private val sessions = mutableMapOf<String, MutableList<AuthorizedWebSocketSession>>()
    private val trackers = mutableMapOf<String, TurnTracker>()

    suspend fun trackTurns() {
        _trackersUpdateChannel.consumeEach {
            combine(observeTrackerEvents(it), observeSessions(it)) { trackerData, sessions ->
                for (session in sessions) {
                    val text = Json.encodeToString(trackerData)
                    try {
                        session.send(text)
                    } catch (ex: Exception) {

                    }
                }
            }.collectLatest { }
        }
    }

    fun saveEncounter(encounter: Encounter) {
        encountersDataSource.addOrUpdate(encounter)

        trackers[encounter.code] = TurnTracker(
            encounter.characters.size,
            encounter.timePerTurn
        )

        _trackersUpdateChannel.offer(encounter.code)
    }

    suspend fun join(code: String, auth: String, webSocketSession: WebSocketSession) {

        val authSession = AuthorizedWebSocketSession(auth, webSocketSession)

        sessions.putIfAbsent(code, mutableListOf())
        sessions[code]?.add(authSession)

        _sessionsUpdateChannel.offer(code)

        authSession.commands.collect { frame ->
            try {
                when (frame.readText()) {
                    "skip" -> skipTurn(auth, code)
                    "resume" -> resume(auth, code)
                    "pause" -> pause(auth, code)
                }
            } catch (exception: Exception) {
                val encounter = encountersDataSource.get(code) ?: return@collect
                val updatedEncounter = encounter.copy(
                    characters = encounter.characters.map {
                        it.copy(ownerID = if (it.ownerID == auth) encounter.ownerID else it.ownerID)
                    }
                )
                encountersDataSource.addOrUpdate(updatedEncounter)

                //todo: make it work
//                pause(encounter.ownerID, code)
                _trackersUpdateChannel.offer(code)
            }
        }
    }

    private suspend fun skipTurn(auth: String, code: String) {
        val turn = trackers[code]?.currentTurn() ?: return
        val encounter = encountersDataSource.get(code) ?: return
        val currentCharacter = encounter.characters[turn]
        if (currentCharacter.ownerID == auth || auth == encounter.ownerID) {
            trackers[code]?.nextTurn()
        }
    }

    private fun pause(auth: String, code: String) {
        val encounter = encountersDataSource.get(code) ?: return

        if (auth == encounter.ownerID) {
            trackers[code]?.pause()
        }
    }

    private fun resume(auth: String, code: String) {
        val encounter = encountersDataSource.get(code) ?: return

        if (auth == encounter.ownerID) {
            trackers[code]?.resume()
        }
    }

    private fun observeTrackerEvents(code: String): Flow<TrackerData> {
        return trackers[code]!!.track()
    }

    private fun observeSessions(code: String): Flow<List<AuthorizedWebSocketSession>> {
        return _sessionsUpdateChannel.asFlow().map { sessions[code] ?: emptyList() }
    }

}