package com.owlsoft.backend.managers

import com.owlsoft.backend.data.Encounter
import com.owlsoft.backend.data.EncounterTrackerData
import com.owlsoft.backend.data.EncountersDataSource
import com.owlsoft.backend.data.Participant
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
import java.util.concurrent.CopyOnWriteArrayList

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class EncountersManager(
    private val encountersDataSource: EncountersDataSource,
    private val logger: Logger
) {

    private val _trackersUpdateChannel = BroadcastChannel<String>(1)
    private val _sessionsUpdateChannel = BroadcastChannel<String>(1)

    private val sessionsData =
        mutableMapOf<String, CopyOnWriteArrayList<AuthorizedWebSocketSession>>()

    private val trackersData = mutableMapOf<String, TurnTracker>()

    suspend fun trackTurns() {
        _trackersUpdateChannel.consumeEach { code ->
            logger.debug("### TRACKERS UPDATE EVENT. CODE = $code")

            combine(observeTracker(code), observeSessions(code)) { trackerData, sessions ->
                logger.debug("### TICK ${trackerData.timerTick}; SESSIONS = [${sessions.map { it.deviceID }}]")
                val encounter = encountersDataSource.get(code) ?: return@combine

                for (session in sessions) {
                    val data = EncounterTrackerData(
                        trackerData.timerTick,
                        trackerData.turnIndex,
                        trackerData.roundIndex,
                        trackerData.isPaused,
                        session.deviceID == encounter.ownerID,
                        session.deviceID == encounter.ownerID || encounter.participants[trackerData.turnIndex].ownerID == session.deviceID,
                        encounter.participants
                            .sortedWith(
                                compareByDescending(Participant::initiative)
                                    .thenByDescending(Participant::dexterity)
                            )
                    )

                    val text = Json.encodeToString(data)

                    try {
                        session.send(text)
                    } catch (ex: Exception) {
                        sessionsData[code]?.remove(session)
                    }
                }
            }
                .collectLatest { }

        }
    }

    fun saveEncounter(encounter: Encounter) {
        encountersDataSource.addOrUpdate(encounter)

        logger.debug("SAVED ENCOUNTER TO DATASOURCE $encounter")


        val tracker = trackersData[encounter.code]

        if (tracker != null) {
            tracker.pause()
            tracker.updateTurnCount(encounter.participants.size)
        } else {

            trackersData[encounter.code] = TurnTracker(
                encounter.timePerTurn,
                encounter.participants.size,
                true
            )

            _trackersUpdateChannel.offer(encounter.code)
        }
    }

    suspend fun join(code: String, auth: String, webSocketSession: WebSocketSession) {

        val authSession = AuthorizedWebSocketSession(auth, webSocketSession)

        sessionsData.putIfAbsent(code, CopyOnWriteArrayList())
        sessionsData[code]?.add(authSession)

        logger.debug("SESSION $auth joining...")
        _sessionsUpdateChannel.offer(code)

        authSession.commands
            .catch { logger.debug("SESSION $auth failed") }
            .collect { frame ->
                try {
                    logger.debug("SESSION $auth input $frame")

                    when (frame.readText()) {
                        "skip" -> skipTurn(auth, code)
                        "resume" -> resume(auth, code)
                        "pause" -> pause(auth, code)
                    }
                } catch (exception: Exception) {

                }
            }
    }

    private fun skipTurn(auth: String, code: String) {
        val turn = trackersData[code]?.currentTurn() ?: return
        val encounter = encountersDataSource.get(code) ?: return
        val currentCharacter = encounter.participants[turn]

        logger.debug("SESSION $auth try skip turn")
        if (currentCharacter.ownerID == auth || auth == encounter.ownerID) {
            trackersData[code]?.nextTurn()
        } else {
            logger.debug("SESSION $auth failed to skip turn")
        }
    }

    private fun pause(auth: String, code: String) {
        val encounter = encountersDataSource.get(code) ?: return

        logger.debug("SESSION $auth try pause")
        if (auth == encounter.ownerID) {
            trackersData[code]?.pause()
        } else {
            logger.debug("SESSION $auth failed to pause")
        }
    }

    private fun resume(auth: String, code: String) {
        val encounter = encountersDataSource.get(code) ?: return

        logger.debug("SESSION $auth try resume")
        if (auth == encounter.ownerID) {
            trackersData[code]?.resume()
        } else {
            logger.debug("SESSION $auth failed to resume")
        }
    }

    private fun observeTracker(code: String): Flow<TrackerData> {
        return trackersData[code]!!.track()
            .catch { logger.debug("FAILED TRACKER FLOW. ${it.message}") }
    }

    private fun observeSessions(code: String): Flow<List<AuthorizedWebSocketSession>> {
        return _sessionsUpdateChannel.asFlow().map { sessionsData[code] ?: emptyList() }
            .catch { logger.debug("FAILED SESSIONS FLOW. ${it.message}") }
    }
}