package com.owlsoft.shared

class UUIDRepository(
    private val storage: Storage,
    private val uuidGenerator: UUIDGenerator
) {
    companion object {
        const val UUID_KEY = "uuid"
    }

    fun getUUID(): String {
        val id = storage.get(UUID_KEY)
        if (id.isNotEmpty()) {
            return id
        }

        val uuid = uuidGenerator.generateID()
        storage.store(UUID_KEY, uuid)
        return uuid
    }
}