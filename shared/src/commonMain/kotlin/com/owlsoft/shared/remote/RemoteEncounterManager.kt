package com.owlsoft.shared.remote

import com.owlsoft.shared.model.EncounterData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RemoteEncounterManager(
    private val remoteEncounterTracker: RemoteEncounterTracker
) {

    fun trackEncounter(): Flow<EncounterData> = remoteEncounterTracker.messages()
        .map { Json.decodeFromString(it) }

    fun skipTurn() = remoteEncounterTracker.skipTurn()

    fun pause() = remoteEncounterTracker.pause()

    fun resume() = remoteEncounterTracker.resume()
}