package com.owlsoft.backend.data

object LocalEncountersDataSource : EncountersDataSource {

    private val encounters = mutableMapOf<String, Encounter>()

    override fun addOrUpdate(encounter: Encounter) {
        encounters[encounter.code] = encounter
    }

    override fun get(code: String) = encounters[code]
}