package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.remote.EncounterAPI

class CreateEncounterUseCase(
    private val encounterAPI: EncounterAPI,
    private val uuidRepository: UUIDRepository
) {
    suspend fun execute(participants: List<Participant>): String {
        val (code, _) = encounterAPI.createEncounter(
            uuidRepository.getUUID(),
            participants
        )
        return code
    }
}