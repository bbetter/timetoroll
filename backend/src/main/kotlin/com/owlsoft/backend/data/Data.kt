package com.owlsoft.backend.data

import kotlinx.serialization.Serializable

@Serializable
class Participant(
    val playerID: String,
    val name: String,
    val initiative: Int,
    val dexterity: Int
)

@Serializable
data class Encounter(
    val code: String = "",
    val ownerID: String = "",
    val singleTurnSeconds: Int = 60,
    val participants: List<Participant> = emptyList(),
)

@Serializable
data class TrackerData(
    val timerTick: Int = 0,
    val turnIndex: Int = 0,
    val roundIndex: Int = 0
)