package com.owlsoft.shared.usecases

import com.owlsoft.shared.managers.EncounterManager
import com.owlsoft.shared.model.Participant


class GetCurrentEncounterParticipantsUseCase(
    private val encounterManager: EncounterManager
) {
    fun execute(): List<Participant> {
        return encounterManager.participants
    }
}