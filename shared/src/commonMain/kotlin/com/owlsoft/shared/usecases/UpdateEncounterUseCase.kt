package com.owlsoft.shared.usecases

import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.shared.remote.EncounterAPI
import io.ktor.http.cio.*

class UpdateEncounterUseCase(
    private val encounterAPI: EncounterAPI
) {
    suspend fun execute(code: String, participants: List<Participant>): RequestResult {
        return try {
            encounterAPI.updateEncounter(code, participants)
            RequestResult.Success(code)
        } catch (ex: Exception) {
            RequestResult.Error(ex.message ?: "")
        }
    }
}