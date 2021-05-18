package com.owlsoft.shared.usecases

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.remote.EncounterAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class CreateEncounterResult {
    object Loading : CreateEncounterResult()
    data class Error(val message: String) : CreateEncounterResult()
    data class Success(val code: String) : CreateEncounterResult()
}

class CreateEncounterUseCase(
    private val encounterAPI: EncounterAPI,
    private val uuidRepository: UUIDRepository
) {

    fun execute(participants: List<Participant>): Flow<CreateEncounterResult> {
        return flow {
            emit(CreateEncounterResult.Loading)
            try {
                val ownerID = uuidRepository.getUUID()
                val (code, _) = encounterAPI.createEncounter(
                    ownerID,
                    participants.map { it.copy(ownerID = ownerID) }
                )
                emit(CreateEncounterResult.Success(code))
            } catch (ex: Exception) {
                emit(CreateEncounterResult.Error(ex.message ?: "Unknown Error"))
            }
        }
    }
}