package com.owlsoft.turntoroll.shared.data

data class EncounterParticipant(
    val name: String,
    val pictureURL: String,
    val ownerID: String,
    val initiative: Int,
    val dexterity: Int
)