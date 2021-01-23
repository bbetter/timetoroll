package com.owlsoft.backend.data

interface EncountersDataSource {
    fun addOrUpdate(encounter: Encounter)
    fun findByCode(code: String): Encounter?
    fun remove(code: String)
}