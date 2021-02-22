package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.*
import com.owlsoft.shared.model.EncounterData
import com.owlsoft.turntoroll.TrackerChannelsHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class EncounterSessionViewModel(
    private val trackerChannelsHolder: TrackerChannelsHolder
) : ViewModel() {

    val trackerLiveData: LiveData<EncounterData> = trackerChannelsHolder.data
        .asFlow()
        .asLiveData()

    fun skipTurn() {
        viewModelScope.launch(Dispatchers.Default) {
            trackerChannelsHolder.commands.send("skip")
        }
    }

    fun resume() {
        viewModelScope.launch(Dispatchers.Default) {
            trackerChannelsHolder.commands.send("resume")
        }
    }

    fun pause() {
        viewModelScope.launch(Dispatchers.Default) {
            trackerChannelsHolder.commands.send("pause")
        }
    }
}