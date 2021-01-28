package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.shared.usecases.CreateEncounterUseCase

class EncounterViewModel(
    private val createEncounterUseCase: CreateEncounterUseCase
) : ViewModel() {
    private val participants: MutableList<Participant> = mutableListOf()

    private val _participantsLiveData = MutableLiveData<List<Participant>>()
    val participantsLiveData: LiveData<List<Participant>> = _participantsLiveData

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

    fun removeParticipant(participant: Participant) {
        participants.remove(participant)
        _participantsLiveData.postValue(participants)
    }

    suspend fun saveEncounter(): RequestResult {
        return createEncounterUseCase.execute(participants)
    }
}