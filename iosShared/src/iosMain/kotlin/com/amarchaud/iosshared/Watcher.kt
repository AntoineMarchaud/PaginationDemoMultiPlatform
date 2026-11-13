package com.amarchaud.iosshared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/** Subscription to a Kotlin flow from Swift. Call [close] to stop receiving values. */
class Watcher internal constructor(private val job: Job) {
    fun close() = job.cancel()
}

/** Collects the flow in [scope] (the ViewModel's, on the main thread) and calls [onEach] for every value. */
internal fun <T> Flow<T>.watch(scope: CoroutineScope, onEach: (T) -> Unit): Watcher =
    Watcher(scope.launch { collect { onEach(it) } })
