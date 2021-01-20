package com.owlsoft.backend.data

object LocalEncountersDataSource : EncountersDataSource {

    private val encounters = mutableListOf<Encounter>()

    override fun add(encounter: Encounter) {
        encounters.add(encounter)
    }

    override fun getByCode(code: String) = encounters
        .firstOrNull { it.code == code }
        ?.let { it.withSortedCharacters() }

    override fun updateByCode(code: String, newEncounter: Encounter) {
        encounters.removeIf { it.code == code }
        encounters.add(newEncounter)
    }

    private fun Encounter.withSortedCharacters() = copy(
        characters = characters.sortedWith(
            compareBy(
                Character::dexterity,
                Character::initiative
            )
        )
    )
}