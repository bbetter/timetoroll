package com.owlsoft.backend.data

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable

@Serializable
class Character(
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

    val characters: List<Character> = emptyList(),
)

@Serializable
data class TrackerData(
    val timerTick: Int = 0,
    val turnIndex: Int = 0,
    val roundIndex: Int = 0,
    val isPaused: Boolean = false
)