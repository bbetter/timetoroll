package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.usecases.CreateEncounterUseCase
import com.owlsoft.shared.usecases.EncounterCreateResult

class EncounterDetailsViewModel(
    private val createEncounterUseCase: CreateEncounterUseCase
) : ViewModel() {
    private val participants: MutableList<Participant> = mutableListOf()

    private val _participantsLiveData = MutableLiveData<List<Participant>>()
    val participantsLiveData: LiveData<List<Participant>> = _participantsLiveData

    suspend fun createEncounter(): EncounterCreateResult {
        return createEncounterUseCase.execute(participants)
    }

    fun addParticipant(name: String, initiative: String, dexterity: String) {
        val participant = Participant(
            "",
            name,
            initiative.toIntOrNull() ?: 0,
            dexterity.toIntOrNull() ?: 0
        )
        participants.add(participant)
        _participantsLiveData.postValue(participants)
    }
}