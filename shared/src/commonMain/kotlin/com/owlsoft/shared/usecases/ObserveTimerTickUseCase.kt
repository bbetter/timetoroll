package com.owlsoft.shared.usecases

import com.owlsoft.shared.managers.EncounterManager
import kotlinx.coroutines.flow.Flow

class ObserveTimerTickUseCase(
    private val encounterManager: EncounterManager
) {
    fun execute(): Flow<Int> {
        return encounterManager.observeTimerTick()
    }
}