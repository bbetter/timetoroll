package com.owlsoft.shared.viewmodel

import com.owlsoft.shared.model.EncounterData
import com.owlsoft.shared.remote.RemoteEncounterTracker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class EncounterSessionViewModel(
    code: String,
    private val onSessionDataUpdated: (EncounterData) -> Unit
) : BaseViewModel(), KoinComponent {

    private var currentData: EncounterData? = null

    private val encounterTracker by inject<RemoteEncounterTracker>(
        parameters = {
            parametersOf("code" to code)
        }
    )

    fun track() {
        scope.launch {
            encounterTracker.start()
            encounterTracker.data()
                .onEach { currentData = it }
                .collect { onSessionDataUpdated(it) }
        }
    }


    fun doTrackerAction() {
        scope.launch {
            val isPaused = currentData?.isPaused ?: false

            if (isPaused) {
                encounterTracker.resume()
            } else {
                encounterTracker.pause()
            }
        }
    }

    fun skipTurn() {
        scope.launch {
            encounterTracker.skipTurn()
        }
    }
}