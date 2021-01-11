package com.owlsoft.shared.managers

import com.owlsoft.shared.model.Participant

import kotlinx.coroutines.flow.Flow

interface EncounterManager {
    var participants: List<Participant>

    suspend fun skipTurn()
    suspend fun resetTimer()

    fun observeActiveParticipant(): Flow<Int>

    fun observeTimerTick(): Flow<Int>
}