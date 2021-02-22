package com.owlsoft.shared.viewmodel

import kotlinx.coroutines.CoroutineScope

actual open class BaseViewModel actual constructor(){
    actual val scope: CoroutineScope
        get() = TODO("Not yet implemented")

    protected actual open fun onCleared() {
    }
}