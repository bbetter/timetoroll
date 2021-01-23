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
            call.respond(HttpStatusCode.NoContent)
            return@get
        }

        val encounter = encountersDataSource.get(code) ?: kotlin.run {
            call.respond(HttpStatusCode.NoContent)
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
    encountersDataSource: EncountersDataSource,
    encountersManager: EncountersManager
) {
    post("$encountersPath/{code}/join") {
        val code = call.parameters["code"] ?: kotlin.run {
            call.respond(HttpStatusCode.NoContent)
            return@post
        }
        val encounter = encountersDataSource.get(code) ?: kotlin.run {
            call.respond(HttpStatusCode.NoContent)
            return@post
        }

        val participant = call.receive<Character>()

        val newEncounter = encounter.copy(
            characters = encounter.characters + participant
        )

        encountersManager.saveEncounter(newEncounter)

        call.respond(HttpStatusCode.OK, encounter)
    }
}