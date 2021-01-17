package com.owlsoft.backend.data

interface EncountersDataSource {
    fun add(encounter: Encounter)
    fun getByCode(code: String): Encounter?
    fun updateByCode(code: String, newEncounter: Encounter)
}