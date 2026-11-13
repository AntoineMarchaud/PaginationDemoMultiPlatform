package com.amarchaud.iosshared

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amarchaud.domain.models.ErrorApiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.plus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class PagingItemsTest {

    private val dispatcher = StandardTestDispatcher()

    /** 3 pages of 2 ints: [0,1] [2,3] [4,5]; records every page it loads. */
    private class IntPagingSource(private val loads: MutableList<Int>) : PagingSource<Int, Int>() {
        override fun getRefreshKey(state: PagingState<Int, Int>): Int? = null

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Int> {
            val page = params.key ?: 0
            loads += page
            return LoadResult.Page(
                data = listOf(page * 2, page * 2 + 1),
                prevKey = null,
                nextKey = if (page < 2) page + 1 else null,
            )
        }
    }

    private val loads = mutableListOf<Int>()
    private var createdSources = 0

    private fun pagerFlow() = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 1, initialLoadSize = 2, enablePlaceholders = false),
        pagingSourceFactory = { createdSources++; IntPagingSource(loads) },
    ).flow

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun emitsItemsOfStaticPagingData() = runTest(dispatcher) {
        val pagingItems = PagingItems(flowOf(PagingData.from(listOf(1, 2, 3))), backgroundScope)
        runCurrent()

        assertEquals(listOf(1, 2, 3), pagingItems.state.value.items)
        assertEquals(LoadStatus.Idle, pagingItems.state.value.refresh)
    }

    @Test
    fun displayingLastItemLoadsNextPageOnce() = runTest(dispatcher) {
        val pagingItems = PagingItems(pagerFlow(), backgroundScope)
        runCurrent()
        assertEquals(listOf(0, 1), pagingItems.state.value.items)

        pagingItems.onItemDisplayed(1)
        pagingItems.onItemDisplayed(1)
        runCurrent()

        assertEquals(listOf(0, 1, 2, 3), pagingItems.state.value.items)
        assertEquals(listOf(0, 1), loads)
    }

    @Test
    fun appendEndReachedWhenNoMorePages() = runTest(dispatcher) {
        val pagingItems = PagingItems(pagerFlow(), backgroundScope)
        runCurrent()

        pagingItems.onItemDisplayed(1)
        runCurrent()
        pagingItems.onItemDisplayed(3)
        runCurrent()
        pagingItems.onItemDisplayed(5)
        runCurrent()

        assertEquals(listOf(0, 1, 2, 3, 4, 5), pagingItems.state.value.items)
        assertEquals(LoadStatus.EndReached, pagingItems.state.value.append)
    }

    @Test
    fun refreshErrorKeepsItemsAndExposesError() = runTest(dispatcher) {
        val pagingData = PagingData.from(
            data = listOf(1, 2),
            sourceLoadStates = LoadStates(
                refresh = LoadState.Error(ErrorApiModel.NoInternetError()),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val pagingItems = PagingItems(flowOf(pagingData), backgroundScope)
        runCurrent()

        assertEquals(listOf(1, 2), pagingItems.state.value.items)
        val refresh = assertIs<LoadStatus.Error>(pagingItems.state.value.refresh)
        assertIs<ErrorApiModel.NoInternetError>(refresh.error)
    }

    @Test
    fun refreshErrorThatIsNotAnApiErrorBecomesGenericError() = runTest(dispatcher) {
        val pagingData = PagingData.from(
            data = emptyList<Int>(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.Error(IllegalStateException("boom")),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val pagingItems = PagingItems(flowOf(pagingData), backgroundScope)
        runCurrent()

        val refresh = assertIs<LoadStatus.Error>(pagingItems.state.value.refresh)
        assertIs<ErrorApiModel.GenericError>(refresh.error)
    }

    @Test
    fun closeStopsCollecting() = runTest(dispatcher) {
        val source = MutableStateFlow(PagingData.from(listOf(1)))
        val pagingItems = PagingItems(source, backgroundScope + Job(backgroundScope.coroutineContext[Job]))
        runCurrent()
        assertEquals(listOf(1), pagingItems.state.value.items)

        pagingItems.close()
        source.value = PagingData.from(listOf(2))
        runCurrent()

        assertEquals(listOf(1), pagingItems.state.value.items)
    }

    @Test
    fun watchReceivesStates() = runTest(dispatcher) {
        val pagingItems = PagingItems(flowOf(PagingData.from(listOf(7, 8))), backgroundScope)
        val seen = mutableListOf<List<Int?>>()
        pagingItems.watch { seen += it.items }
        runCurrent()

        assertEquals(listOf(7, 8), seen.last())
    }

    @Test
    fun refreshReloadsFromFirstPage() = runTest(dispatcher) {
        val pagingItems = PagingItems(pagerFlow(), backgroundScope)
        runCurrent()
        assertEquals(1, createdSources)

        pagingItems.refresh()
        runCurrent()

        assertEquals(2, createdSources)
        assertEquals(listOf(0, 1), pagingItems.state.value.items)
    }
}
