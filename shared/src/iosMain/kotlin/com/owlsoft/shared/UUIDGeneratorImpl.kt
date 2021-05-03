package com.owlsoft.shared

import platform.Foundation.NSUUID

class UUIDGeneratorImpl : UUIDGenerator {
    override fun generateID(): String {
        return NSUUID.UUID().toString()
    }
}