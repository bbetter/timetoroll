import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

fun main() {

    val encounters = mutableListOf<Encounter>()

    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            json()
        }

        install(WebSockets) {
        }

        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }

        install(CallLogging) {
        }

        routing {

            route("/encounters") {
                //get by code
                get("{code}") {
                    val code = call.parameters["code"] ?: kotlin.run {
                        call.respond(HttpStatusCode.NoContent)
                        return@get
                    }

                    val encounter = encounters.firstOrNull { it.code == code } ?: kotlin.run {
                        call.respond(HttpStatusCode.NoContent)
                        return@get
                    }

                    call.respond(encounter)
                }

                //join
                post("{code}/join") {
                    val code = call.parameters["code"] ?: kotlin.run {
                        call.respond(HttpStatusCode.NoContent)
                        return@post
                    }
                    var encounter = encounters.firstOrNull { it.code == code } ?: kotlin.run {
                        call.respond(HttpStatusCode.NoContent)
                        return@post
                    }

                    val currentParticipants = encounter.participants
                    val participant = call.receive<Participant>()

                    val alreadyInGame =
                        currentParticipants.any { it.ownerID == participant.ownerID }

                    if (alreadyInGame) {
                        call.respond(HttpStatusCode.Created)
                        return@post
                    }

                    encounter = encounter.copy(
                        participants = encounter.participants + participant
                    )

                    call.respond(HttpStatusCode.OK)
                }

                //create encounter

                post {
                    val currentTimeMillis = System.currentTimeMillis()
                    val requestEncounter = call.receive<Encounter>()
                    val resultEncounter = requestEncounter.copy(
                        code = UUID.randomUUID().toString().take(5),
                        startTimeStamp = currentTimeMillis
                    )
                    encounters.add(resultEncounter)
                    call.respond(resultEncounter)
                }

                webSocket("{code}") {
                    val code = call.parameters["code"] ?: kotlin.run {
                        call.respond(HttpStatusCode.NoContent)
                        return@webSocket
                    }

                    val encounter = encounters.firstOrNull { it.code == code } ?: kotlin.run {
                        return@webSocket
                    }

                    val currentSecondsInEncounter =
                        (System.currentTimeMillis() - encounter.startTimeStamp) / 1000

                    val singleTurnInSeconds = 60

                    val turnsInEncounter = currentSecondsInEncounter / singleTurnInSeconds

                    val currentSecondsOnTimer =
                        singleTurnInSeconds - (currentSecondsInEncounter - turnsInEncounter * singleTurnInSeconds)

                    var timerCountDown = currentSecondsOnTimer.toInt()
                    var turnIndex = turnsInEncounter.toInt()
                    var roundsCount = 1

                    while (true) {

                        val roundInfo = RoundInfo(
                            timerCountDown,
                            turnIndex,
                            roundsCount
                        )

                        if (timerCountDown-- == 0) {
                            timerCountDown = currentSecondsOnTimer.toInt()
                            turnIndex++
                        }

                        if (turnIndex == encounter.participants.size) {
                            turnIndex = 0
                            roundsCount++
                        }

                        val encounterData = Json.encodeToString(roundInfo)
                        outgoing.send(Frame.Text(encounterData))
                        delay(1000)
                    }
                }
            }
        }
    }.start(wait = true)
}