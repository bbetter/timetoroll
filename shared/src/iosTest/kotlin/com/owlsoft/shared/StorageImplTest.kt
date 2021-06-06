package com.owlsoft.shared

import platform.Foundation.NSUserDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

class StorageImplTest {

    private val storage = StorageImpl(NSUserDefaults(suiteName = "TTR"))

    @Test
    fun `value can be stored`(){
        storage.store("key", "value")
        assertEquals("value", storage.get("key"))
    }
}