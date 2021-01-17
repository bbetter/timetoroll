package com.owlsoft.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val playerID: String,
    val name: String,
    val initiative: Int,
    val dexterity: Int
)