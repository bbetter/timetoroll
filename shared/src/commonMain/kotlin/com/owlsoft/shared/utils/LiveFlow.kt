package com.owlsoft.shared.utils

import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus

fun <T> Flow<T>.asLiveFlow(
    scope: CoroutineScope
): LiveFlow<T> = LiveFlow(this, scope)

class LiveFlow<T>(
    private val origin: Flow<T>,
    private val scope: CoroutineScope
) : Flow<T> by origin {

    var value: T? = null
        private set

    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()

        onEach {
            value = it
            block(it)
        }
            .stateIn(scope, SharingStarted.Eagerly, value)
            .launchIn(scope + job)

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}