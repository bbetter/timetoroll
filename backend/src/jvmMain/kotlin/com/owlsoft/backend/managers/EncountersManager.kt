package com.owlsoft.backend.managers

import com.owlsoft.backend.data.EncountersDataSource
import com.owlsoft.shared.model.Encounter
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import org.slf4j.Logger

class EncountersManager(
    private val dataSource: EncountersDataSource,
    private val logger: Logger
) {

    private val encounterCreatedChannel = BroadcastChannel<String>(1)
    private val turnTrackers = mutableMapOf<String, EncounterTurnTracker>()

    suspend fun listenToTrackerCreation() {
        encounterCreatedChannel.consumeEach {
            turnTrackers[it]?.track()
        }
    }

    fun getEncounter(code: String) = dataSource.findByCode(code)

    fun saveEncounter(encounter: Encounter) {
        dataSource.addOrUpdate(encounter)

        val tracker = turnTrackers[encounter.code]

        if (tracker != null) {
            tracker.pause()
            tracker.updateEncounter(encounter)
            logger.debug("### ENCOUNTER ${encounter.code} updated tracker ")
        } else {
            val turnTracker = TurnTracker(
                encounter.timePerTurn,
                encounter.participants.size,
                true
            )
            turnTrackers[encounter.code] = EncounterTurnTracker(
                turnTracker,
                encounter
            )
            logger.debug("### ENCOUNTER ${encounter.code} created tracker ")
            encounterCreatedChannel.offer(encounter.code)
        }
    }

    suspend fun join(code: String, auth: String, webSocketSession: WebSocketSession) {
        val authSession = AuthorizedWebSocketSession(auth, webSocketSession)
        turnTrackers[code]?.newSession(
            authSession
        ) {
            turnTrackers[code]?.cancel()
            turnTrackers.remove(code)
        }
    }
}


