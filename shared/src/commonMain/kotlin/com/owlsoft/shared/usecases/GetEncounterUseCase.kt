package com.owlsoft.shared.usecases

import com.owlsoft.shared.model.Character
import com.owlsoft.shared.model.Encounter
import com.owlsoft.shared.remote.EncounterAPI

class GetEncounterUseCase(
    private val encounterAPI: EncounterAPI
) {
    suspend fun execute(code: String): Encounter {
        val encounter = encounterAPI.getEncounterByCode(code)

        return encounter.copy(
            characters = encounter.characters.sortedWith(
                compareBy(
                    Character::initiative,
                    Character::dexterity
                )
            )
        )
    }
}