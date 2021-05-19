package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.remote.EncounterAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class JoinEncounterResult {
    object Loading : JoinEncounterResult()
    object Success : JoinEncounterResult()
    class Error(val message: String) : JoinEncounterResult()
}

class JoinEncounterUseCase(
    private val identityRepository: UUIDRepository,
    private val encounterAPI: EncounterAPI
) {
    fun execute(
        code: String,
        participants: List<Participant>
    ): Flow<JoinEncounterResult> {
        return flow {
            emit(JoinEncounterResult.Loading)
            try {
                val deviceID = identityRepository.getUUID()
                val deviceParticipants = participants.map { it.copy(ownerID = deviceID) }
                encounterAPI.updateEncounter(code, deviceParticipants)
                emit(JoinEncounterResult.Success)
            } catch (ex: Exception) {
                emit(JoinEncounterResult.Error(ex.message ?: "Unknown Error"))
            }
        }
    }
}