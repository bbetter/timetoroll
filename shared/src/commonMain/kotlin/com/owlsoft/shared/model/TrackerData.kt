package com.owlsoft.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackerData(
    val timerTick: Int = 0,
    val turnIndex: Int = 0,
    val roundIndex: Int = 0,
    val isPaused: Boolean = false
)