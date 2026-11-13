package com.amarchaud.iosshared

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class WatcherTest {

    @Test
    fun receivesEveryValueUntilClosed() = runTest {
        val flow = MutableStateFlow(1)
        val seen = mutableListOf<Int>()

        val watcher = flow.watch(backgroundScope) { seen += it }
        runCurrent()
        flow.value = 2
        runCurrent()
        watcher.close()
        flow.value = 3
        runCurrent()

        assertEquals(listOf(1, 2), seen)
    }

    @Test
    fun closeIsIdempotent() = runTest {
        val watcher = MutableStateFlow(1).watch(backgroundScope) {}
        watcher.close()
        watcher.close()
    }
}
