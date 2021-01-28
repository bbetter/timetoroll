package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.remote.EncounterAPI

class CreateEncounterUseCase(
    private val encounterAPI: EncounterAPI,
    private val uuidRepository: UUIDRepository
) {

    suspend fun execute(participants: List<Participant>): RequestResult {
        return try {
            val ownerID = uuidRepository.getUUID()
            val (code, _) = encounterAPI.createEncounter(
                ownerID,
                participants.map { it.copy(ownerID = ownerID) }
            )
            RequestResult.Success(code)
        } catch (ex: Exception) {
            RequestResult.Error(ex.message ?: "Unknown Error")
        }
    }
}