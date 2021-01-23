package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.*
import com.owlsoft.shared.model.EncounterData
import com.owlsoft.shared.remote.RemoteEncounterManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EncounterViewModel(
    private val remoteEncounterManager: RemoteEncounterManager,
) : ViewModel() {

    val trackerLiveData: LiveData<EncounterData> = remoteEncounterManager.track()
        .flowOn(Dispatchers.IO)
        .asLiveData()

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