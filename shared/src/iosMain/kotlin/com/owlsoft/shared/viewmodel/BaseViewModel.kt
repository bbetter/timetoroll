package com.owlsoft.shared.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

actual open class BaseViewModel actual constructor() {
    private val parentJob = SupervisorJob()

    actual val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    protected actual fun onCleared() {
        parentJob.cancelChildren()
    }
}