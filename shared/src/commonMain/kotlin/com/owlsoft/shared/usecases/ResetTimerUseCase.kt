package com.owlsoft.shared.usecases

import com.owlsoft.shared.managers.EncounterManager
import kotlinx.coroutines.flow.Flow

class ResetTimerUseCase(
    private val encounterManager: EncounterManager
) {
    suspend fun execute() {
        return encounterManager.resetTimer()
    }
}