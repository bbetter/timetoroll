package com.owlsoft.shared

actual interface Storage{
    actual fun store(key: String, value: String)
    actual fun get(key: String): String
}