package com.owlsoft.shared

import platform.Foundation.NSLog
import platform.Foundation.NSUUID

class UUIDGeneratorImpl : UUIDGenerator {
    init {
        NSLog("%s", "Creating uuid generator...")
    }

    override fun generateID(): String {
        return NSUUID.UUID().toString()
    }
}