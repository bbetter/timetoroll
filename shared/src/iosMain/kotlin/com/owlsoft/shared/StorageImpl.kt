package com.owlsoft.shared

import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

class StorageImpl(
    val defaults: NSUserDefaults
) : Storage {
    override fun store(key: String, value: String) {
        defaults.setValue(value, key)
    }

    override fun get(key: String): String {
        return defaults.stringForKey(key) ?: ""
    }
}