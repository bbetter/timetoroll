package com.owlsoft.turntoroll

import com.owlsoft.shared.model.EncounterData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.*

@OptIn(ExperimentalCoroutinesApi::class)
class LocalRemoteEncounterTracker {
    val commands: BroadcastChannel<String> = ConflatedBroadcastChannel()
    val data: BroadcastChannel<EncounterData> = ConflatedBroadcastChannel()
}