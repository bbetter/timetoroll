package com.owlsoft.backend.data

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val ownerID: String,
    val name: String,
    val initiative: Int,
    val dexterity: Int
)

@Serializable
data class Encounter(
    val code: String = "",
    val ownerID: String = "",
    val timePerTurn: Int = 60,

    val participants: List<Participant>,
)

@Serializable
data class EncounterTrackerData(
    val tick: Int,
    val turnIndex: Int,
    val roundIndex: Int,
    val isPaused: Boolean,
    val isPlayPauseAllowed: Boolean,
    val isSkipTurnAllowed: Boolean,
    val participants: List<Participant>
)