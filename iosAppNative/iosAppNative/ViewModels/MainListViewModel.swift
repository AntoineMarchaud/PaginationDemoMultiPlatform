import Foundation
import Observation
import Shared

/// Discover screen: the paged users of the shared domain (GetRandomUsersUseCase), as cards.
@MainActor
@Observable
final class MainListViewModel {

    /// Every Paging position: users and placeholders (rows not loaded yet).
    private(set) var slots: [CardSlot] = []
    private(set) var refreshStatus: any LoadStatus = LoadStatusLoading.shared
    private(set) var appendStatus: any LoadStatus = LoadStatusIdle.shared

    var isFirstLoad: Bool { slots.isEmpty && refreshStatus is LoadStatusLoading }
    var refreshError: ErrorApiModel? { (refreshStatus as? LoadStatusError)?.error }
    var appendError: ErrorApiModel? { (appendStatus as? LoadStatusError)?.error }
    var isAppending: Bool { appendStatus is LoadStatusLoading }

    @ObservationIgnored private let pagingItems: PagingItems<UserModel>
    @ObservationIgnored private var watcher: Watcher?
    @ObservationIgnored private var visibleIDs = Set<String>()
    @ObservationIgnored private var wasRefreshing = false
    @ObservationIgnored private var refreshWaiters: [CheckedContinuation<Void, Never>] = []

    init(useCases: SharedUseCases = SharedUseCases()) {
        pagingItems = PagingItems(flow: useCases.getRandomUsers.run())
        // called on the main thread by Kotlin
        watcher = pagingItems.watch { [weak self] state in
            self?.apply(state)
        }
    }

    isolated deinit {
        watcher?.close()
        pagingItems.close()
    }

    /// A slot came on screen: lets Paging load the rows around it (next, previous or missing page).
    func slotAppeared(_ id: CardSlot.ID) {
        visibleIDs.insert(id)
        hintVisibleCards()
    }

    func slotDisappeared(_ id: CardSlot.ID) {
        visibleIDs.remove(id)
    }

    /// Reloads from the first page; returns when the reload is over (pull-to-refresh spinner).
    func refresh() async {
        pagingItems.refresh()
        await withCheckedContinuation { refreshWaiters.append($0) }
    }

    func retry() {
        pagingItems.retry()
    }

    private func apply(_ state: PagingItemsState<UserModel>) {
        slots = cardSlots(from: state.items)
        refreshStatus = state.refresh
        appendStatus = state.append

        let refreshing = state.refresh is LoadStatusLoading
        if wasRefreshing && !refreshing {
            refreshWaiters.forEach { $0.resume() }
            refreshWaiters.removeAll()
        }
        wasRefreshing = refreshing

        // slots already on screen don't get onAppear again after a reload:
        // tell Paging what is visible (same as LazyPagingItems reading the visible items)
        hintVisibleCards()
    }

    private func hintVisibleCards() {
        guard let range = visibleIndexRange(of: slots, visibleIDs: visibleIDs) else { return }
        pagingItems.onItemDisplayed(index: Int32(range.lowerBound))
        pagingItems.onItemDisplayed(index: Int32(range.upperBound))
    }
}

