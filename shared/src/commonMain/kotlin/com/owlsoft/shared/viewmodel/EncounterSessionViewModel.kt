package com.owlsoft.shared.viewmodel

import com.owlsoft.shared.di.AppLogger
import com.owlsoft.shared.remote.RemoteEncounterTracker
import com.owlsoft.shared.utils.asLiveFlow
import io.ktor.client.features.logging.*
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class EncounterSessionViewModel(
    private val code: String
) : BaseViewModel(), KoinComponent {

    private val tracker by inject<RemoteEncounterTracker>(
        parameters = {
            parametersOf("code" to code)
        }
    )

    val data = tracker.data()
        .asLiveFlow(scope)

    fun doTrackerAction() {
        scope.launch {

            val isPaused = data.value?.isPaused ?: false

            if (isPaused) {
                tracker.resume()
            } else {
                tracker.pause()
            }
        }
    }

    fun skipTurn() {
        scope.launch {
            tracker.skipTurn()
        }
    }
}