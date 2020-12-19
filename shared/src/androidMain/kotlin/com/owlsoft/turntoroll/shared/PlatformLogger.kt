package com.owlsoft.turntoroll.shared

import android.util.Log

actual class PlatformLogger {
    actual fun log(tag: String, message: String) {
        Log.d(tag, message)
    }
}