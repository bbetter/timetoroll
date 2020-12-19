package com.owlsoft.turntoroll.shared.data

import kotlinx.coroutines.flow.Flow

interface EncounterManager {
    suspend fun getParticipants(): List<EncounterParticipant>
    suspend fun next()

    fun observeActiveParticipant(): Flow<Int>
    fun observeTimerTick(seconds: Int): Flow<Int>
}