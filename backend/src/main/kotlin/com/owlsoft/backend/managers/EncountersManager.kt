package com.owlsoft.backend.managers

import com.owlsoft.backend.data.Encounter
import com.owlsoft.backend.data.EncounterTrackerData
import com.owlsoft.backend.data.EncountersDataSource
import com.owlsoft.backend.data.Participant
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import java.lang.Exception
import java.util.concurrent.CopyOnWriteArrayList

class EncountersManager(
    private val dataSource: EncountersDataSource,
    private val logger: Logger
) {

    private val _encounterBroadcastChannel = BroadcastChannel<String>(1)

    private val sessionsData =
        mutableMapOf<String, CopyOnWriteArrayList<AuthorizedWebSocketSession>>()

    private val trackersData = mutableMapOf<String, TurnTracker>()

    suspend fun trackTurns() {
        _encounterBroadcastChannel.consumeEach { code ->

            logger.debug("### ENCOUNTER UPDATE EVENT. CODE = $code")

            observeTracker(code)
                .collectLatest { trackerData ->

                    val encounter = dataSource.findByCode(code) ?: return@collectLatest
                    val sessions = sessionsData[code] ?: return@collectLatest
                    val participants = encounter.participants
                        .sortedWith(
                            compareByDescending(Participant::initiative)
                                .thenByDescending(Participant::dexterity)
                        )
                    logger.debug("### ENCOUNTER $code. TICK = ${trackerData.timerTick}")

                    for (session in sessions) {
                        val isAdminSession = session.deviceID == encounter.ownerID
                        val isCurrentSession = participants[trackerData.turnIndex].ownerID == session.deviceID

                        val data = EncounterTrackerData(
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
                            session.send(text)
                        } catch (ex: Exception) {

                        }
                    }
                }
        }
    }

    fun getEncounter(code: String) = dataSource.findByCode(code)

    suspend fun saveEncounter(encounter: Encounter) {
        dataSource.addOrUpdate(encounter)

        val tracker = trackersData[encounter.code]

        if (tracker != null) {
            tracker.pause()
            tracker.updateTurnCount(encounter.participants.size)
            logger.debug("UPDATE TRACKER ${encounter.code}")
        } else {

            trackersData[encounter.code] = TurnTracker(
                encounter.timePerTurn,
                encounter.participants.size,
                true
            )
            logger.debug("NEW TRACKER ${encounter.code}")
        }
    }

    suspend fun join(code: String, auth: String, webSocketSession: WebSocketSession) {

        val authSession = AuthorizedWebSocketSession(auth, webSocketSession)

        sessionsData.putIfAbsent(code, CopyOnWriteArrayList())
        sessionsData[code]?.add(authSession)

        logger.debug("ENCOUNTER $code SESSION ${authSession.deviceID} started. ")

        _encounterBroadcastChannel.send(code)

        authSession.commands
            .collect { frame ->
                try {
                    when (frame.readText()) {
                        "skip" -> skipTurn(auth, code)
                        "resume" -> resume(auth, code)
                        "pause" -> pause(auth, code)
                    }
                } catch (exception: Exception) {

                }
            }

        logger.debug("SESSION ${authSession.deviceID} completed")

        val sessions = sessionsData[code] ?: return

        sessions.remove(authSession)

        if (sessions.isEmpty()) {
            dataSource.remove(code)
            trackersData[code]?.cancel()
            trackersData.remove(code)
        }

        _encounterBroadcastChannel.send(code)
    }

    private fun skipTurn(auth: String, code: String) {
        val turn = trackersData[code]?.currentTurn() ?: return
        val encounter = dataSource.findByCode(code) ?: return

        val currentCharacter = encounter.participants[turn]

        logger.debug("SESSION $auth try skip turn")
        if (currentCharacter.ownerID == auth || auth == encounter.ownerID) {
            trackersData[code]?.nextTurn()
        } else {
            logger.debug("SESSION $auth failed to skip turn")
        }
    }

    private fun pause(auth: String, code: String) {
        val encounter = dataSource.findByCode(code) ?: return

        logger.debug("SESSION $auth try pause")
        if (auth == encounter.ownerID) {
            trackersData[code]?.pause()
        } else {
            logger.debug("SESSION $auth failed to pause")
        }
    }

    private fun resume(auth: String, code: String) {
        val encounter = dataSource.findByCode(code) ?: return

        logger.debug("SESSION $auth try resume")
        if (auth == encounter.ownerID) {
            trackersData[code]?.resume()
        } else {
            logger.debug("SESSION $auth failed to resume")
        }
    }

    private fun observeTracker(code: String): Flow<TrackerData> {
        if (trackersData[code] == null) {
            logger.debug("NO TRACKER $code")
            return flow { }
        }

        return trackersData[code]!!.track()
            .catch { logger.debug("FAILED TRACKER FLOW. $code; ${it.message}") }
    }

//    private fun observeSessions(code: String): Flow<List<AuthorizedWebSocketSession>> {
//        return _sessionsUpdateChannel.map { sessionsData[code] ?: emptyList() }
//            .catch { log.debug("FAILED SESSIONS FLOW. ${it.message}") }
//    }
}