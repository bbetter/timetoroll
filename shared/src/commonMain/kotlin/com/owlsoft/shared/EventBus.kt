package com.owlsoft.shared

import kotlinx.coroutines.flow.MutableSharedFlow

open class EventBus<T> {
    val events = MutableSharedFlow<T>(1, 1)

    protected suspend fun send(event: T) {
        events.emit(event)
    }
}