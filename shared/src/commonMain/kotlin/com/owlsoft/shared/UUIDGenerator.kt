package com.owlsoft.shared

expect interface UUIDGenerator {
    actual fun generateID(): String
}