package com.amarchaud.iosshared

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import com.amarchaud.domain.models.ErrorApiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Loading status of one direction of a paged list, readable from Swift. */
sealed interface LoadStatus {
    data object Loading : LoadStatus
    data object Idle : LoadStatus
    data object EndReached : LoadStatus
    data class Error(val error: ErrorApiModel) : LoadStatus
}

data class PagingItemsState<T>(
    /**
     * Every position of the list, like `LazyPagingItems`: `null` is a placeholder (a row not loaded
     * yet). Positions stay stable when pages are reloaded, and index `i` is the one to pass to
     * [PagingItems.onItemDisplayed].
     */
    val items: List<T?> = emptyList(),
    val refresh: LoadStatus = LoadStatus.Loading,
    val append: LoadStatus = LoadStatus.Idle,
)

/**
 * Presents a `Flow<PagingData>` as a plain observable list for SwiftUI, which cannot use
 * `LazyPagingItems`. Same mechanism as Compose: a [PagingDataPresenter].
 * Runs on the main thread; call [close] when the screen goes away.
 */
class PagingItems<T : Any> internal constructor(flow: Flow<PagingData<T>>, private val scope: CoroutineScope) {

    constructor(flow: Flow<PagingData<T>>) : this(flow, MainScope())

    private val _state = MutableStateFlow(PagingItemsState<T>())
    val state: StateFlow<PagingItemsState<T>> = _state.asStateFlow()

    private val presenter = object : PagingDataPresenter<T>() {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<T>) = Unit
    }

    init {
        scope.launch { flow.collectLatest { presenter.collectFrom(it) } }
        scope.launch { presenter.onPagesUpdatedFlow.collect { publish() } }
        scope.launch { presenter.loadStateFlow.collect { publish() } }
    }

    /** Call when the item at [index] is shown: lets Paging load the next page when needed. */
    fun onItemDisplayed(index: Int) {
        if (index in 0 until presenter.size) presenter[index]
    }

    fun refresh() = presenter.refresh()

    fun retry() = presenter.retry()

    /** Calls [onEach] with the current state, then with every change. */
    fun watch(onEach: (PagingItemsState<T>) -> Unit): Watcher = state.watch(scope, onEach)

    /** Stops loading and observing. The instance can't be used afterwards. */
    fun close() = scope.cancel()

    private fun publish() {
        val loadStates = presenter.loadStateFlow.value
        _state.value = PagingItemsState(
            items = presenter.snapshot().toList(),
            refresh = loadStates?.refresh.toStatus(),
            append = loadStates.appendStatus(),
        )
    }

    private fun LoadState?.toStatus(): LoadStatus = when (this) {
        is LoadState.Loading -> LoadStatus.Loading
        is LoadState.Error -> LoadStatus.Error(error as? ErrorApiModel ?: ErrorApiModel.GenericError())
        is LoadState.NotLoading, null -> LoadStatus.Idle
    }

    private fun CombinedLoadStates?.appendStatus(): LoadStatus {
        val append = this?.append
        return if (append is LoadState.NotLoading && append.endOfPaginationReached) LoadStatus.EndReached else append.toStatus()
    }
}
