package com.owlsoft.backend.data

interface EncountersDataSource {
    fun addOrUpdate(encounter: Encounter)
    fun get(code: String): Encounter?
}