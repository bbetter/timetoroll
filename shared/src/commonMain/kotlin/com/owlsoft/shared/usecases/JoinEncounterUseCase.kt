package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.remote.EncounterAPI
import com.owlsoft.shared.remote.TrackerAPI

class JoinEncounterUseCase(
    private val idUUIDRepository: UUIDRepository,
    private val encounterAPI: EncounterAPI
) {
    suspend fun execute(code: String, name: String, initiative: Int, dexterity: Int) {
        val encounterParticipant = Participant(
            idUUIDRepository.getUUID(),
            name,
            initiative,
            dexterity
        )
        encounterAPI.join(code, encounterParticipant)
    }
}