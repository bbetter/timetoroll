package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owlsoft.shared.model.EncounterData
import com.owlsoft.shared.remote.RemoteEncounterManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class EncounterViewModel(
    private val remoteEncounterManager: RemoteEncounterManager,
) : ViewModel() {

    private val _trackerLiveData = MutableLiveData<EncounterData>()
    val trackerLiveData: LiveData<EncounterData> = _trackerLiveData

    fun track() {
        viewModelScope.launch(Dispatchers.Default) {

            remoteEncounterManager.trackEncounter()
                .distinctUntilChanged()
                .flowOn(Dispatchers.IO)
                .collectLatest { _trackerLiveData.postValue(it) }
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