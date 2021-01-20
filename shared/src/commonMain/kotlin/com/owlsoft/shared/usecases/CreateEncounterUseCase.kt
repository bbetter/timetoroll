package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.Character
import com.owlsoft.shared.remote.EncounterAPI

class CreateEncounterUseCase(
    private val encounterAPI: EncounterAPI,
    private val uuidRepository: UUIDRepository
) {
    suspend fun execute(characters: List<Character>): String {
        val (code, _) = encounterAPI.createEncounter(
            uuidRepository.getUUID(),
            characters
        )
        return code
    }
}