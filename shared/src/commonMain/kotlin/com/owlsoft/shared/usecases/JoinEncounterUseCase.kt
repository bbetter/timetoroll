package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.remote.EncounterAPI

sealed class JoinEncounterResult {
    object Success : JoinEncounterResult()
    class Error(val message: String) : JoinEncounterResult()
}

class JoinEncounterUseCase(
    private val identityRepository: UUIDRepository,
    private val encounterAPI: EncounterAPI
) {
    suspend fun execute(
        code: String,
        participants: List<Participant>
    ): JoinEncounterResult {
        return try {
            val deviceID = identityRepository.getUUID()
            val deviceParticipants = participants.map { it.copy(ownerID = deviceID) }

            encounterAPI.join(code, deviceParticipants)
            JoinEncounterResult.Success
        } catch (ex: Exception) {
            JoinEncounterResult.Error(ex.message ?: "Unknown Error")
        }
    }
}