package com.owlsoft.shared

import android.content.SharedPreferences

class StorageImpl(
    private val sharedPreferences: SharedPreferences
) : Storage {
    override fun store(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun get(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }
}