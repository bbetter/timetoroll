package com.owlsoft.shared.viewmodel

import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.shared.usecases.CreateEncounterUseCase
import com.owlsoft.shared.usecases.GetEncounterUseCase
import com.owlsoft.shared.usecases.UpdateEncounterUseCase
import com.owlsoft.shared.utils.asLiveFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class EncounterViewModel : BaseViewModel(), KoinComponent {

    private val createEncounterUseCase by inject<CreateEncounterUseCase>()
    private val updateEncounterUseCase by inject<UpdateEncounterUseCase>()
    private val getEncounterUseCase by inject<GetEncounterUseCase>()

    private val _data = MutableStateFlow(emptyList<Participant>())
    val data = _data.asLiveFlow(scope)

    fun requestParticipants(code: String) {
        scope.launch {
            val encounter = getEncounterUseCase.execute(code)
            _data.emit(encounter.participants)
        }
    }

    fun addParticipant(name: String, ini: Int, dex: Int) {
        scope.launch {
            val participant = Participant(
                "",
                name,
                ini,
                dex
            )
            _data.emit(_data.value + participant)
        }
    }

    fun removeParticiapant(participant: Participant) {
        scope.launch {
            _data.emit(_data.value - participant)
        }
    }

    fun dataAt(index: Int) = _data.value[index]

    fun dataCount() = _data.value.size

    fun update(code: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        scope.launch {
            when (val result = updateEncounterUseCase.execute(code, _data.value)) {
                is RequestResult.Success -> {
                    onSuccess()
                }
                is RequestResult.Error -> {
                    onError(result.message)
                }
            }
        }
    }

    fun create(onSuccess: (String) -> Unit, onError: (String) -> Unit) {

        scope.launch {
            when (val result = createEncounterUseCase.execute(_data.value)) {
                is RequestResult.Success -> {
                    onSuccess(result.code)
                }
                is RequestResult.Error -> {
                    onError(result.message)
                }
            }
        }
    }
}