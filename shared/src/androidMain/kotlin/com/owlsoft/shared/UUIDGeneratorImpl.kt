package com.owlsoft.shared

import java.util.*

class UUIDGeneratorImpl : UUIDGenerator {
    override fun generateID(): String {
        return UUID.randomUUID().toString()
    }
}