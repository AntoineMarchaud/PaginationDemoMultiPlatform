package com.amarchaud.iosshared

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * The app's real setup: Room's paging source (items before / after the loaded range) and
 * PagingConfig(pageSize = 20), so placeholders are on. Every RemoteMediator page inserts rows,
 * which invalidates the source: it reloads around the last displayed position.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PagingItemsPlaceholdersTest {

    private val dispatcher = StandardTestDispatcher()
    private val itemCount = 200
    private var current: RoomLikeSource? = null

    /** Same arithmetic as androidx.room.paging (LimitOffsetPagingSource). */
    private inner class RoomLikeSource : PagingSource<Int, Int>() {
        override fun getRefreshKey(state: PagingState<Int, Int>): Int? =
            state.anchorPosition?.let { maxOf(0, it - state.config.initialLoadSize / 2) }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Int> {
            val key = params.key ?: 0
            val limit = if (params is LoadParams.Prepend && key < params.loadSize) key else params.loadSize
            val offset = when (params) {
                is LoadParams.Prepend -> if (key < params.loadSize) 0 else key - params.loadSize
                is LoadParams.Append -> key
                is LoadParams.Refresh -> if (key >= itemCount - params.loadSize) maxOf(0, itemCount - params.loadSize) else key
            }
            val data = (offset until minOf(itemCount, offset + limit)).toList()
            val next = offset + data.size
            return LoadResult.Page(
                data = data,
                prevKey = if (offset <= 0 || data.isEmpty()) null else offset,
                nextKey = if (data.isEmpty() || data.size < limit || next >= itemCount) null else next,
                itemsBefore = offset,
                itemsAfter = maxOf(0, itemCount - next),
            )
        }
    }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /** Scrolls down to the 80th item like the Swift grid, then a database insert invalidates the source. */
    private fun TestScope.scrolledThenInvalidated(): PagingItems<Int> {
        val pagingItems = PagingItems(
            Pager(PagingConfig(pageSize = 20)) { RoomLikeSource().also { current = it } }.flow,
            backgroundScope,
        )
        runCurrent()
        for (index in 0..79) {
            pagingItems.onItemDisplayed(index)
            runCurrent()
        }
        current!!.invalidate()
        runCurrent()
        return pagingItems
    }

    @Test
    fun positionsStayStableAfterInvalidation() = runTest(dispatcher) {
        val items = scrolledThenInvalidated().state.value.items

        // placeholders keep every item at its position: the grid doesn't jump
        assertEquals(itemCount, items.size)
        assertEquals(79, items[79])
    }

    @Test
    fun displayingTheTopPlaceholderLoadsTheFirstItems() = runTest(dispatcher) {
        val pagingItems = scrolledThenInvalidated()
        assertNull(pagingItems.state.value.items[0])

        pagingItems.onItemDisplayed(0)
        runCurrent()

        assertEquals(0, pagingItems.state.value.items[0])
    }

    @Test
    fun displayingTheLastLoadedItemLoadsTheNextPage() = runTest(dispatcher) {
        val pagingItems = scrolledThenInvalidated()
        val lastLoaded = pagingItems.state.value.items.indexOfLast { it != null }

        pagingItems.onItemDisplayed(lastLoaded)
        runCurrent()

        assertNotNull(pagingItems.state.value.items[lastLoaded + 1])
    }
}
