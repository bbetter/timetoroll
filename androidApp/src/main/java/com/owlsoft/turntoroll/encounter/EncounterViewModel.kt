package com.owlsoft.turntoroll.encounter

import androidx.lifecycle.*
import com.owlsoft.shared.model.EncounterData
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.usecases.GetEncounterUseCase
import com.owlsoft.shared.usecases.ObserveRoundUseCase
import com.owlsoft.shared.usecases.SkipTurnUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class EncounterViewModel(
    observeRoundUseCase: ObserveRoundUseCase,
    private val skipTurnUseCase: SkipTurnUseCase,
    private val getEncounterUseCase: GetEncounterUseCase,
) : ViewModel() {

    private val _participantsLiveData = MutableLiveData<List<Participant>>()
    val participantsLiveData: LiveData<List<Participant>> = _participantsLiveData

    val encounterData: LiveData<EncounterData> = observeRoundUseCase.execute()
        .flowOn(Dispatchers.IO)
        .asLiveData(viewModelScope.coroutineContext)

    fun requestParticipants(code: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = getEncounterUseCase.execute(code)
            _participantsLiveData.postValue(result.participants)
        }
    }

    fun skipTurn() {
        viewModelScope.launch(Dispatchers.Default) {
            skipTurnUseCase.execute()
        }
    }
}