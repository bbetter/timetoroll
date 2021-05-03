package com.owlsoft.shared

import platform.Foundation.NSLog
import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

class StorageImpl(
    private val defaults: NSUserDefaults
) : Storage {
    init {
        NSLog("%s", "Creating storage...")
    }
    override fun store(key: String, value: String) {
        defaults.setValue(value = value, forKey = key)
    }

    override fun get(key: String): String {
        return defaults.stringForKey(key) ?: ""
    }
}