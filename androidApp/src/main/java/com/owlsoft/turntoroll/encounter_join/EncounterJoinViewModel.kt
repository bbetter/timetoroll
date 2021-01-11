package com.owlsoft.turntoroll.encounter_join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owlsoft.shared.usecases.JoinEncounterUseCase
import kotlinx.coroutines.launch

class EncounterJoinViewModel(
    private val joinEncounterUseCase: JoinEncounterUseCase
) : ViewModel() {

    fun joinEncounter(code: String, name: String, initiative: String, dexterity: String) {
        viewModelScope.launch {
            joinEncounterUseCase.execute(
                code,
                name,
                initiative.toIntOrNull() ?: 0,
                dexterity.toIntOrNull() ?: 0
            )
        }
    }
}