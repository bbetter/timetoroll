package com.owlsoft.turntoroll.encounter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.RoundInfo
import com.owlsoft.shared.usecases.GetCurrentEncounterParticipantsUseCase
import com.owlsoft.shared.usecases.ObserveRoundUseCase
import com.owlsoft.shared.usecases.SetupEncounterUseCase
import com.owlsoft.shared.usecases.SkipTurnUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class EncounterViewModel(
    private val observeRoundUseCase: ObserveRoundUseCase,
    private val getCurrentEncounterParticipantsUseCase: GetCurrentEncounterParticipantsUseCase,
    private val skipTurnUseCase: SkipTurnUseCase,
    private val setupEncounterUseCase: SetupEncounterUseCase,
) : ViewModel() {

    private val _roundLiveData = MutableLiveData<RoundInfo>()
    val roundLiveData: LiveData<RoundInfo> = _roundLiveData

    private val _participantsLiveData = MutableLiveData<List<Participant>>()
    val participantsLiveData: LiveData<List<Participant>> = _participantsLiveData

    fun observeRoundInfo(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeRoundUseCase.execute(code)
                .flowOn(Dispatchers.IO)
                .collect {
                    Log.d("DEBUG", "### $it")
                    _roundLiveData.postValue(it)
                }
        }
    }

    fun requestParticipants(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            setupEncounterUseCase.execute(code)

            val result = getCurrentEncounterParticipantsUseCase.execute()
            _participantsLiveData.postValue(result)
        }
    }

    fun skip() {
        viewModelScope.launch {
            skipTurnUseCase.execute()
        }
    }
}