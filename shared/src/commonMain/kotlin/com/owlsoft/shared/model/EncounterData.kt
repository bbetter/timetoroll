package com.owlsoft.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class EncounterData(
    val tick: Int,
    val turnIndex: Int,
    val roundIndex: Int,
    val isPaused: Boolean,
    val isPlayPauseAllowed: Boolean,
    val isSkipTurnAllowed: Boolean,
    val participants: List<Participant>
)