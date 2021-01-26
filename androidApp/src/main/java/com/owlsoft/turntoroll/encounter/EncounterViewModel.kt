package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EncounterViewModel(
//    private val remoteEncounterTracker: RemoteEncounterTracker,
) : ViewModel() {

//    val trackerLiveData: LiveData<EncounterData> = remoteEncounterTracker.track()
//        .flowOn(Dispatchers.IO)
//        .asLiveData()

    fun skipTurn() {
        viewModelScope.launch(Dispatchers.Default) {
//            remoteEncounterTracker.skipTurn()
        }
    }

    fun resume() {
        viewModelScope.launch(Dispatchers.Default) {
//            remoteEncounterTracker.resume()
        }
    }

    fun pause() {
        viewModelScope.launch(Dispatchers.Default) {
//            remoteEncounterTracker.pause()
        }
    }
}