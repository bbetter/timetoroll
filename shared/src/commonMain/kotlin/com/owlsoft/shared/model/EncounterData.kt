package com.owlsoft.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class EncounterData(
    val timerTick: Int = 0,
    val turnIndex: Int = 0,
    val roundIndex: Int = 0
)