package com.owlsoft.shared.usecases

import com.owlsoft.shared.model.RoundInfo
import com.owlsoft.shared.remote.EncounterTurnAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ObserveRoundUseCase(
    private val turnAPI: EncounterTurnAPI
) {
    fun execute(code: String): Flow<RoundInfo> {
        return turnAPI.observeTurn(code)
            .map { Json.decodeFromString(it) }
    }
}