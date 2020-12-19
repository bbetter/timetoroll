package com.owlsoft.turntoroll.shared

import platform.Foundation.NSLog

actual class PlatformLogger {
    actual fun log(tag: String, message: String) {
        NSLog("%s", "$tag# $message")
    }
}