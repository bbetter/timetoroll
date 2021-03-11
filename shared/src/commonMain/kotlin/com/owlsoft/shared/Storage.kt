package com.owlsoft.shared

expect interface    Storage{
    fun store(key: String, value: String)
    fun get(key: String): String
}