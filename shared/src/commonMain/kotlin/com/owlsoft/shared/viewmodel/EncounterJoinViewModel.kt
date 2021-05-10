package com.owlsoft.shared.viewmodel

import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.usecases.JoinEncounterResult
import com.owlsoft.shared.usecases.JoinEncounterUseCase
import com.owlsoft.shared.usecases.RollDiceUseCase
import com.owlsoft.shared.utils.asLiveFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EncounterJoinViewModel : BaseViewModel(), KoinComponent {

    private val joinEncounterUseCase by inject<JoinEncounterUseCase>()
    private val rollDiceUseCase by inject<RollDiceUseCase>()

    private val _data = MutableStateFlow<List<Participant>>(emptyList())
    val data = _data.asLiveFlow(scope)

    fun addParticipant(name: String, ini: Int, dex: Int) {
        val participant = Participant(
            "",
            name,
            ini,
            dex
        )
        scope.launch {
            _data.emit(_data.value + participant)
        }
    }

    fun removeParticipant(participant: Participant) {
        scope.launch {
            _data.emit(_data.value - participant)
        }
    }

    fun dataAt(index: Int) = _data.value[index]

    fun dataCount() = _data.value.size

    fun join(code: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        scope.launch {
            when (val result = joinEncounterUseCase.execute(code, _data.value)) {
                is JoinEncounterResult.Success -> {
                    onSuccess()
                }
                is JoinEncounterResult.Error -> {
                    onError(result.message)
                }
                else -> {
                }
            }
        }
    }

    fun rollInitiative() = rollDiceUseCase.execute()
}