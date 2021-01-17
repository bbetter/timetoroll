package com.owlsoft.backend.routes

import com.owlsoft.backend.data.*
import com.owlsoft.backend.managers.TrackersManager

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

const val encountersPath = "/encounters"

fun Route.encounterByCodeRoute(
    encountersDataSource: EncountersDataSource,
) {
    get("$encountersPath/{code}") {
        val code = call.parameters["code"] ?: kotlin.run {
            call.respond(HttpStatusCode.NoContent)
            return@get
        }

        val encounter = encountersDataSource.getByCode(code) ?: kotlin.run {
            call.respond(HttpStatusCode.NoContent)
            return@get
        }


        call.respond(encounter)
    }
}

fun Route.createEncounterRoute(
    encountersDataSource: EncountersDataSource
) {
    post(encountersPath) {
        val requestEncounter = call.receive<Encounter>()

        val resultEncounter = requestEncounter.copy(
            code = UUID.randomUUID().toString().take(5),
        )

        encountersDataSource.add(resultEncounter)

        TrackersManager.newTracker(resultEncounter)
        call.respond(resultEncounter)
    }
}

fun Route.joinEncounterRoute(
    encountersDataSource: EncountersDataSource
) {
    post("$encountersPath/{code}/join") {
        val code = call.parameters["code"] ?: kotlin.run {
            call.respond(HttpStatusCode.NoContent)
            return@post
        }
        val encounter = encountersDataSource.getByCode(code) ?: kotlin.run {
            call.respond(HttpStatusCode.NoContent)
            return@post
        }

        val participant = call.receive<Participant>()

        val newEncounter = encounter.copy(
            participants = encounter.participants + participant
        )

        encountersDataSource.updateByCode(
            code,
            newEncounter
        )

        TrackersManager.updateTrackerConfig(code, newEncounter)

        call.respond(HttpStatusCode.OK, encounter)
    }
}