package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.shared.usecases.CreateEncounterUseCase
import com.owlsoft.shared.usecases.GetEncounterUseCase
import com.owlsoft.shared.usecases.UpdateEncounterUseCase
import kotlinx.coroutines.launch

class EncounterViewModel(
    private val createEncounterUseCase: CreateEncounterUseCase,
    private val updateEncounterUseCase: UpdateEncounterUseCase,
    private val getEncounterUseCase: GetEncounterUseCase
) : ViewModel() {
    private val participants: MutableList<Participant> = mutableListOf()

    private val _participantsLiveData = MutableLiveData<List<Participant>>()
    val participantsLiveData: LiveData<List<Participant>> = _participantsLiveData

    fun requestParticipants(code: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                val encounter = getEncounterUseCase.execute(code)
                _participantsLiveData.postValue(encounter.participants)
            }
        }
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

    fun removeParticipant(participant: Participant) {
        participants.remove(participant)
        _participantsLiveData.postValue(participants)
    }

    suspend fun saveEncounter(code: String): RequestResult {
        return if (code.isEmpty()) {
            createEncounterUseCase.execute(participants)
        } else {
            updateEncounterUseCase.execute(code, participants)
        }
    }
}