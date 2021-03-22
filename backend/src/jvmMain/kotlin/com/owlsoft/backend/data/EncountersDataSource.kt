package com.owlsoft.backend.data

import com.owlsoft.shared.model.Encounter

interface EncountersDataSource {
    fun addOrUpdate(encounter: Encounter)
    fun findByCode(code: String): Encounter?
    fun remove(code: String)
}