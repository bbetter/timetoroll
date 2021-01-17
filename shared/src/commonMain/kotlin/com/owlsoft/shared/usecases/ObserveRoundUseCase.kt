package com.owlsoft.shared.usecases

import com.owlsoft.shared.model.EncounterData
import com.owlsoft.shared.remote.TrackerAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ObserveRoundUseCase(
    private val trackerAPI: TrackerAPI
) {
    fun execute(): Flow<EncounterData> {
        return trackerAPI.listenToEvents()
            .map { Json.decodeFromString(it) }
    }
}