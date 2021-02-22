package com.owlsoft.shared.viewmodel

import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.shared.usecases.CreateEncounterUseCase
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import platform.Foundation.NSLog

class EncounterViewModel : BaseViewModel(), KoinComponent {

    private val createEncounterUseCase by inject<CreateEncounterUseCase>()

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

    fun create(onSuccess: (String) -> Unit) {
        scope.launch {
            when (val result = createEncounterUseCase.execute(participants)) {
                is RequestResult.Success -> {
                    NSLog("SUCCESS: %s", result.toString())
                    onSuccess(result.code)
                }
                is RequestResult.Error -> NSLog("ERROR: %s", result.toString())
            }
        }
    }
}