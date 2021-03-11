package com.owlsoft.shared.remote

import com.owlsoft.shared.Configuration
import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.EncounterData
import com.owlsoft.shared.sockets.AppSocketSession
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RemoteEncounterTracker(
    encounterCode: String,
    uuidRepository: UUIDRepository
) {

    private val session = AppSocketSession.create(
        uuidRepository.getUUID(),
        "ws://${Configuration.serverUrl}/tracker/$encounterCode"
    ).apply {
        connect()
    }

    fun data() = session.incoming
        .mapLatest { Json.decodeFromString<EncounterData>(it) }

    fun skipTurn() = session.send("skip")

    fun pause() = session.send("pause")

    fun resume() = session.send("resume")

    fun complete() {
        session.close()
    }
}