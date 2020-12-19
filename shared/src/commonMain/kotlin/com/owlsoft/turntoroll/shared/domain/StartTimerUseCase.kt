package com.owlsoft.turntoroll.shared.domain

import com.owlsoft.turntoroll.shared.data.EncounterManager
import kotlinx.coroutines.flow.Flow

class StartTimerUseCase(
    private val encounterManager: EncounterManager
) {
    fun execute(seconds: Int): Flow<Int> {
        return encounterManager.observeTimerTick(seconds)
    }
}