package com.owlsoft.shared.usecases

import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.Encounter
import com.owlsoft.shared.remote.EncounterAPI

class GetEncounterUseCase(
    private val encounterAPI: EncounterAPI
) {
    suspend fun execute(code: String): Encounter {
        val encounter = encounterAPI.getEncounterByCode(code)

        return encounter.copy(
            participants = encounter.participants.sortedWith(
                compareBy(
                    Participant::initiative,
                    Participant::dexterity
                )
            )
        )
    }
}