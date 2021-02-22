package com.owlsoft.shared.viewmodel

import kotlinx.coroutines.CoroutineScope

actual open class BaseViewModel actual constructor() {
    actual val scope: CoroutineScope
        get() = TODO()

    protected actual open fun onCleared() {
    }

}