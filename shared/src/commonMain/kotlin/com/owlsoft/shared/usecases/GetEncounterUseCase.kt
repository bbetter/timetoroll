package com.owlsoft.shared.usecases

import com.owlsoft.shared.model.Encounter
import com.owlsoft.shared.remote.EncounterAPI


class GetEncounterUseCase(
    private val encounterAPI: EncounterAPI
) {
    suspend fun execute(code: String): Encounter {
        return encounterAPI.getEncounterByCode(code)
    }
}