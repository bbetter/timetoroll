package com.owlsoft.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Encounter(
    val code: String = "",
    val ownerID: String,
    val participants: List<Participant>,
    val startTimeStamp: Long
)