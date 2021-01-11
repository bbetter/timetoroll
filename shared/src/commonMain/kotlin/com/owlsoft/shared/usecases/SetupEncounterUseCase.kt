package com.owlsoft.shared.usecases

import com.owlsoft.shared.managers.EncounterManager
import com.owlsoft.shared.remote.EncounterAPI

class SetupEncounterUseCase(
    private val encounterAPI: EncounterAPI,
    private val encounterManager: EncounterManager
) {
    suspend fun execute(code: String) {
        val encounter = encounterAPI.getEncounterByCode(code)

        encounterManager.participants = encounter.participants
    }
}