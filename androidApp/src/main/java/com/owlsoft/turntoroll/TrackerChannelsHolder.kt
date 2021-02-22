package com.owlsoft.turntoroll

import com.owlsoft.shared.model.EncounterData
import kotlinx.coroutines.channels.*

class TrackerChannelsHolder {
    val commands: BroadcastChannel<String> = ConflatedBroadcastChannel()
    val data: BroadcastChannel<EncounterData> = ConflatedBroadcastChannel()
}