package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.*
import com.owlsoft.shared.model.Character
import com.owlsoft.shared.model.TrackerData
import com.owlsoft.shared.remote.RemoteEncounterManager
import com.owlsoft.shared.usecases.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class EncounterViewModel(
    private val remoteEncounterManager: RemoteEncounterManager,
    private val getEncounterUseCase: GetEncounterUseCase,
) : ViewModel() {

    private val _participantsLiveData = MutableLiveData<List<Character>>()
    val participantsLiveData: LiveData<List<Character>> = _participantsLiveData

    val trackerData: LiveData<TrackerData> = remoteEncounterManager.trackEncounter()
        .flowOn(Dispatchers.IO)
        .asLiveData(viewModelScope.coroutineContext)

    fun requestParticipants(code: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = getEncounterUseCase.execute(code)
            _participantsLiveData.postValue(result.characters)
        }
    }

    fun skipTurn() {
        viewModelScope.launch(Dispatchers.Default) {
            remoteEncounterManager.skipTurn()
        }
    }

    fun resume() {
        viewModelScope.launch(Dispatchers.Default) {
            remoteEncounterManager.resume()
        }
    }

    fun pause() {
        viewModelScope.launch(Dispatchers.Default) {
            remoteEncounterManager.pause()
        }
    }
}