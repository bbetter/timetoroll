package com.owlsoft.turntoroll.shared.domain

import com.owlsoft.turntoroll.shared.data.EncounterManager

class ObserveActiveParticipantUseCase(
    private val encounterManager: EncounterManager
) {
    fun execute() = encounterManager.observeActiveParticipant()
}