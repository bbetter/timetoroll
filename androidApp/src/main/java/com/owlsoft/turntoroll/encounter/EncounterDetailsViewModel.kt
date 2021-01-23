package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.owlsoft.shared.model.Character
import com.owlsoft.shared.usecases.CreateEncounterUseCase
import com.owlsoft.shared.usecases.EncounterCreateResult

class EncounterDetailsViewModel(
    private val createEncounterUseCase: CreateEncounterUseCase
) : ViewModel() {
    private val characters: MutableList<Character> = mutableListOf()

    private val _participantsLiveData = MutableLiveData<List<Character>>()
    val participantsLiveData: LiveData<List<Character>> = _participantsLiveData

    suspend fun createEncounter(): EncounterCreateResult {
        return createEncounterUseCase.execute(characters)
    }

    fun addParticipant(character: Character) {
        characters.add(character)
        _participantsLiveData.postValue(characters)
    }
}