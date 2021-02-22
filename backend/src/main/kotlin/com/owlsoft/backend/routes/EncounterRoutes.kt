package com.owlsoft.backend.routes

import com.owlsoft.backend.data.*
import com.owlsoft.backend.managers.EncountersManager

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

const val encountersPath = "/encounters"

fun Route.encounterByCodeRoute(
    encountersDataSource: EncountersDataSource
) {
    get("$encountersPath/{code}") {
        val code = call.parameters["code"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val encounter = encountersDataSource.findByCode(code) ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }


        call.respond(encounter)
    }
}

fun Route.createEncounterRoute(
    encountersManager: EncountersManager
) {
    post(encountersPath) {
        val requestEncounter = call.receive<Encounter>()

        val resultEncounter = requestEncounter.copy(
            code = UUID.randomUUID().toString().take(5),
        )

        encountersManager.saveEncounter(resultEncounter)

        call.respond(resultEncounter)
    }
}

fun Route.joinEncounterRoute(
    encountersManager: EncountersManager
) {
    put("$encountersPath/{code}") {
        val code = call.parameters["code"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@put
        }

        val encounter = encountersManager.getEncounter(code) ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@put
        }

        val newParticipants = call.receive<List<Participant>>()

        if (newParticipants.isNotEmpty()) {

            val newEncounter = encounter.copy(
                participants = encounter.participants + newParticipants
            )

            encountersManager.saveEncounter(newEncounter)
        }

        call.respond(HttpStatusCode.OK, encounter)
    }
}