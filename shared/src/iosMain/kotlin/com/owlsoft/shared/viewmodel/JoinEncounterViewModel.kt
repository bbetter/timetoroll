package com.owlsoft.shared.viewmodel

import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.usecases.JoinEncounterResult
import com.owlsoft.shared.usecases.JoinEncounterUseCase
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import platform.Foundation.NSLog

class JoinEncounterViewModel : BaseViewModel(), KoinComponent {

    private val joinEncounterUseCase by inject<JoinEncounterUseCase>()

    private val participants = mutableListOf<Participant>()

    fun add(name: String, ini: Int, dex: Int) {
        val participant = Participant(
            "",
            name,
            ini,
            dex
        )
        participants.add(participant)
    }

    fun removeAt(index: Int) {
        participants.removeAt(index)
    }

    fun dataAt(index: Int) = participants[index]

    fun dataCount() = participants.size

    fun join(code: String, onSuccess: () -> Unit) {
        scope.launch {
            when (val result = joinEncounterUseCase.execute(code, participants)) {
                is JoinEncounterResult.Success -> {
                    onSuccess()
                }
                is JoinEncounterResult.Error -> NSLog("%s", result.message)
            }
        }
    }
}