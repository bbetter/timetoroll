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
import org.slf4j.Logger
import java.lang.Exception
import kotlin.math.log

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class EncountersManager(
    private val encountersDataSource: EncountersDataSource,
    private val logger: Logger
) {

    private val _trackersUpdateChannel = BroadcastChannel<String>(1)
    private val _sessionsUpdateChannel = BroadcastChannel<String>(1)

    private val sessions = mutableMapOf<String, MutableList<AuthorizedWebSocketSession>>()
    private val trackers = mutableMapOf<String, TurnTracker>()

    suspend fun listenToTrackerEvents() {
        _trackersUpdateChannel.consumeEach {
            combine(observeTrackerEvents(it), observeSessions(it)) { trackerData, sessions ->
                logger.debug("### TICK $trackerData; SESSIONS = [$sessions]")
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

    fun createTracker(encounter: Encounter) {
        trackers[encounter.code] = TurnTracker(
            encounter.characters.size,
            encounter.timePerTurn
        )
        _trackersUpdateChannel.offer(encounter.code)
    }

    suspend fun updateTracker(newEncounter: Encounter) {
        trackers[newEncounter.code]?.updateConfig(
            newEncounter.characters.size,
            newEncounter.timePerTurn
        )
    }

    suspend fun join(code: String, auth: String, webSocketSession: WebSocketSession) {

        val authSession = AuthorizedWebSocketSession(auth, webSocketSession)
        sessions.putIfAbsent(code, mutableListOf())
        sessions[code]?.add(authSession)

        logger.debug("JOIN WITH $code; AUTH = $auth; SESSIONS = [${sessions[code]}]")

        _sessionsUpdateChannel.offer(code)

        authSession.commands.collect { frame ->
            when (frame.readText()) {
                "skip" -> skipTurn(auth, code)
                "resume" -> resume(auth, code)
                "pause" -> pause(auth, code)
            }
        }
    }

    private suspend fun skipTurn(auth: String, code: String) {
        val turn = trackers[code]?.currentTurn() ?: return
        val encounter = encountersDataSource.getByCode(code) ?: return
        val currentCharacter = encounter.characters[turn]
        if (currentCharacter.ownerID == auth || auth == encounter.ownerID) {
            trackers[code]?.nextTurn()
        }
    }

    private fun pause(auth: String, code: String) {
        val encounter = encountersDataSource.getByCode(code) ?: return

        if (auth == encounter.ownerID) {
            trackers[code]?.pause()
        }
    }

    private fun resume(auth: String, code: String) {
        val encounter = encountersDataSource.getByCode(code) ?: return

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