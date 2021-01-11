package com.owlsoft.shared.usecases

import com.owlsoft.shared.managers.EncounterManager
import kotlinx.coroutines.flow.Flow

class SkipTurnUseCase(
    private val encounterManager: EncounterManager
) {
    suspend fun execute() {
        encounterManager.skipTurn()
        encounterManager.resetTimer()
    }
}