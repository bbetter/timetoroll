package com.owlsoft.turntoroll.shared.domain

import com.owlsoft.turntoroll.shared.data.EncounterManagerImpl
import com.owlsoft.turntoroll.shared.data.EncounterParticipant

class GetEncounterParticipantsUseCase(
    private val encounterManager: EncounterManagerImpl
) {
    suspend fun execute(): List<EncounterParticipant> {
        return encounterManager.getParticipants()
    }
}