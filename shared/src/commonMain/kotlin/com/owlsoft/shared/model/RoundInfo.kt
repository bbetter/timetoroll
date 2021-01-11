package com.owlsoft.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class RoundInfo(
    val tickTime: Long,
    val activeParticipantIndex: Int,
    val roundIndex: Int,
)