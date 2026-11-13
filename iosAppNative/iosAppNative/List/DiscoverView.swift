import Shared
import SwiftUI

/// Discover: adaptive grid of people (phone, tablet, foldable), infinite scroll, pull-to-refresh.
struct DiscoverView: View {

    let viewModel: MainListViewModel
    let zoom: Namespace.ID

    var body: some View {
        ScrollView {
            LazyVGrid(columns: [GridItem(.adaptive(minimum: 160), spacing: 12)], spacing: 12) {
                if viewModel.isFirstLoad {
                    ForEach(0..<8, id: \.self) { index in
                        UserCardView(card: .placeholder(index))
                            .redacted(reason: .placeholder)
                    }
                } else {
                    ForEach(viewModel.slots) { slot in
                        slotView(slot)
                            .onAppear { viewModel.slotAppeared(slot.id) }
                            .onDisappear { viewModel.slotDisappeared(slot.id) }
                    }
                }
            }
            .padding(.horizontal, 16)

            footer
        }
        .background(Color(.systemGroupedBackground))
        .refreshable { await viewModel.refresh() }
        .overlay {
            if viewModel.slots.isEmpty, let error = viewModel.refreshError {
                ContentUnavailableView {
                    Label("Couldn’t load people", systemImage: "icloud.slash")
                } description: {
                    Text(errorMessage(error))
                } actions: {
                    Button("Try again") { viewModel.retry() }
                        .buttonStyle(.glassProminent)
                }
            }
        }
        .safeAreaInset(edge: .bottom) {
            if !viewModel.slots.isEmpty, let error = viewModel.refreshError {
                ErrorBanner(message: String(localized: "Couldn’t refresh: \(errorMessage(error))")) {
                    viewModel.retry()
                }
                .padding(.horizontal, 16)
                .frame(maxWidth: 640)
            }
        }
        .navigationTitle("Discover")
        .navigationSubtitle("People from around the world")
        .toolbarTitleDisplayMode(.large)
    }

    @ViewBuilder
    private func slotView(_ slot: CardSlot) -> some View {
        switch slot {
        case .user(let card):
            NavigationLink(value: card.localId) {
                UserCardView(card: card)
            }
            .buttonStyle(.plain)
            .matchedTransitionSource(id: card.localId, in: zoom)
        case .placeholder(let index):
            // a row Paging hasn't loaded yet: same size as a card, at the same position
            UserCardView(card: .placeholder(index))
                .redacted(reason: .placeholder)
        }
    }

    @ViewBuilder
    private var footer: some View {
        if let error = viewModel.appendError {
            ErrorBanner(message: String(localized: "Couldn’t load more: \(errorMessage(error))")) {
                viewModel.retry()
            }
            .frame(maxWidth: 640)
            .padding(16)
        } else if viewModel.isAppending {
            ProgressView()
                .padding(24)
        }
    }
}
