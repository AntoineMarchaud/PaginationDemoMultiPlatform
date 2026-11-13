import Foundation
import Shared

/// One position of the Discover grid: a user, or a placeholder for a row Paging hasn't loaded yet.
/// Showing placeholders keeps every card at the position Paging knows (like LazyPagingItems).
enum CardSlot: Identifiable, Equatable {
    case user(UserCard)
    case placeholder(index: Int)

    var id: String {
        switch self {
        case .user(let card): "u\(card.localId)"
        case .placeholder(let index): "p\(index)"
        }
    }

    var card: UserCard? {
        if case .user(let card) = self { card } else { nil }
    }
}

/// Paging positions to grid slots. Kotlin `List<UserModel?>` arrives as `[Any]`, with `NSNull` for placeholders.
func cardSlots(from items: [Any]) -> [CardSlot] {
    items.enumerated().map { index, item in
        (item as? UserModel).map { .user($0.card) } ?? .placeholder(index: index)
    }
}

/// Positions of the first and last visible slots, nil when none of them is on screen.
func visibleIndexRange(of slots: [CardSlot], visibleIDs: Set<String>) -> ClosedRange<Int>? {
    guard !visibleIDs.isEmpty,
          let first = slots.firstIndex(where: { visibleIDs.contains($0.id) }),
          let last = slots.lastIndex(where: { visibleIDs.contains($0.id) }) else { return nil }
    return first...last
}
