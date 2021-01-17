package com.owlsoft.backend.data

import java.util.*

object LocalEncountersDataSource : EncountersDataSource {

    private val encounters = mutableListOf<Encounter>()

    override fun add(encounter: Encounter) {
        encounters.add(encounter)
    }

    override fun getByCode(code: String): Encounter? {
        return encounters.firstOrNull { it.code == code }
    }

    @Synchronized
    override fun updateByCode(code: String, newEncounter: Encounter) {
        encounters.removeIf { it.code == code }
        encounters.add(newEncounter)
    }

}