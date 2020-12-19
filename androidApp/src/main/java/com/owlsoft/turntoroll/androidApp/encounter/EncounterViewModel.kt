package com.owlsoft.turntoroll.androidApp.encounter

import androidx.lifecycle.*
import com.owlsoft.turntoroll.shared.data.EncounterManagerImpl
import com.owlsoft.turntoroll.shared.domain.GetEncounterParticipantsUseCase
import com.owlsoft.turntoroll.shared.domain.ObserveActiveParticipantUseCase
import com.owlsoft.turntoroll.shared.domain.StartTimerUseCase

class EncounterViewModel(
    private val getEncounterParticipantsUseCase: GetEncounterParticipantsUseCase,
    private val observeActiveParticipantUseCase: ObserveActiveParticipantUseCase,
    private val startTimerUseCase: StartTimerUseCase
) : ViewModel() {

    companion object {
        const val SECONDS = 60
    }

    val timerLiveData =
        startTimerUseCase.execute(SECONDS).asLiveData()

    val participantsLiveData = liveData {
        emit(getEncounterParticipantsUseCase.execute())
    }

    val activeParticipantLiveData =
        observeActiveParticipantUseCase.execute().asLiveData()
}