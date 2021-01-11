package com.owlsoft.shared.managers

import com.owlsoft.shared.model.Participant
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class, FlowPreview::class)
class EncounterManagerImpl : EncounterManager {

    private var _activeParticipantIndex = 0
    private val activeParticipantFlow = MutableStateFlow(_activeParticipantIndex)

    private val _timerConfigurationChannel = ConflatedBroadcastChannel(60)

    private val timerFlow = _timerConfigurationChannel
        .asFlow()
        .flatMapLatest { seconds ->
            var counter = seconds
            callbackFlow {
                while (isActive) {
                    if (counter == 0) {
                        counter = seconds
                        skipTurn()
                    }
                    send(counter--)
                    delay(1000L)
                }
            }
        }

    private val participantsCount
        get() = participants.size

    override var participants: List<Participant> = emptyList()

    override suspend fun skipTurn() {
        if (_activeParticipantIndex == participantsCount - 1) {
            _activeParticipantIndex = 0
        } else {
            _activeParticipantIndex++
        }

        activeParticipantFlow.value = _activeParticipantIndex
    }

    override fun observeTimerTick(): Flow<Int> {
        return timerFlow
    }

    override fun observeActiveParticipant(): Flow<Int> {
        return activeParticipantFlow.asStateFlow()
    }

    override suspend fun resetTimer() {
        _timerConfigurationChannel.send(60)
    }
}