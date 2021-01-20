package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.Character
import com.owlsoft.shared.remote.EncounterAPI

class JoinEncounterUseCase(
    private val idUUIDRepository: UUIDRepository,
    private val encounterAPI: EncounterAPI
) {
    suspend fun execute(code: String, name: String, initiative: Int, dexterity: Int) {
        val encounterParticipant = Character(
            idUUIDRepository.getUUID(),
            name,
            initiative,
            dexterity
        )
        encounterAPI.join(code, encounterParticipant)
    }
}