package com.owlsoft.shared.remote

import com.owlsoft.shared.Configuration
import com.owlsoft.shared.model.Encounter
import com.owlsoft.shared.model.Participant
import io.ktor.client.*
import io.ktor.client.request.*

class EncounterAPI(
    private val httpClient: HttpClient
) {
    private val baseUrl = "http://${Configuration.serverUrl}"

    suspend fun getEncounterByCode(code: String): Encounter {
        return httpClient.get("$baseUrl/encounters/$code") {
            jsonHeader()
        }
    }

    suspend fun createEncounter(
        ownerID: String,
        participants: List<Participant>
    ): Encounter {
        return httpClient.post("$baseUrl/encounters") {
            jsonHeader()
            body = Encounter(
                code = "",
                ownerID,
                participants.map { it.copy(ownerID = ownerID) },
                60
            )
        }
    }

    suspend fun updateEncounter(code: String, participants: List<Participant>): Encounter {
        return httpClient.put("$baseUrl/encounters/$code") {
            jsonHeader()
            body = participants
        }
    }
}