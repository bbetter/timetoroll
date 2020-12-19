package com.owlsoft.turntoroll.shared.data

import com.owlsoft.turntoroll.shared.PlatformLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlin.native.concurrent.ThreadLocal

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class EncounterManagerImpl : EncounterManager {

    private val logger = PlatformLogger()

    private var _activeParticipantIndex = 0
    private val activeParticipantFlow = MutableStateFlow(_activeParticipantIndex)

    private val participants: MutableList<EncounterParticipant> = mutableListOf(
        EncounterParticipant("Archie", "", "", 10, 4),
        EncounterParticipant("Cookie Girl", "", "", 12, 2)
    )

    private val participantsCount
        get() = participants.size


    override suspend fun getParticipants() = participants
        .sortedWith(
            compareByDescending(EncounterParticipant::initiative)
                .thenByDescending(EncounterParticipant::dexterity)
        )

    override suspend fun next() {
        if (_activeParticipantIndex == participantsCount - 1) {
            _activeParticipantIndex = 0
        } else {
            _activeParticipantIndex++
        }

        activeParticipantFlow.value = _activeParticipantIndex
    }

    override fun observeTimerTick(timerSeconds: Int): Flow<Int> {
        return channelFlow {
            var counter = timerSeconds
            if (counter == -1) return@channelFlow
            while (isActive) {
                if (counter == 0) {
                    counter = timerSeconds
                    next()
                }
                send(counter--)
                delay(1000L)
            }
        }
    }

    override fun observeActiveParticipant(): Flow<Int> {
        return activeParticipantFlow.asStateFlow()
    }
}