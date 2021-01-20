package com.owlsoft.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Encounter(
    val code: String = "",
    val ownerID: String,
    val characters: List<Character>,
    val timePerTurn: Int = 60,
)