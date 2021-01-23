package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.remote.EncounterAPI

sealed class EncounterCreateResult {
    class Success(val code: String) : EncounterCreateResult()
    class Error(val message: String) : EncounterCreateResult()
}

class CreateEncounterUseCase(
    private val encounterAPI: EncounterAPI,
    private val uuidRepository: UUIDRepository
) {

    suspend fun execute(participants: List<Participant>): EncounterCreateResult {
        return try {
            val ownerID = uuidRepository.getUUID()
            val (code, _) = encounterAPI.createEncounter(
                ownerID,
                participants.map { it.copy(ownerID = ownerID) }
            )
            EncounterCreateResult.Success(code)
        } catch (ex: Exception) {
            EncounterCreateResult.Error(ex.message ?: "Unknown Error")
        }
    }
}