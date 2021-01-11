package com.owlsoft.shared.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual class EncounterTurnAPI {
    actual fun observeTurn() = flow<String> { }
}