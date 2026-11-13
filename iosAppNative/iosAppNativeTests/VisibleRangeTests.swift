import Foundation
import Testing
import Shared
@testable import paginationnative

/// The grid shows every Paging position (placeholders included), so a card's index is the one Paging expects.
struct CardSlotsTests {

    private func user(_ localId: Int64) -> UserModel {
        UserModel(
            localId: localId, gender: nil, name: NameModel(title: nil, first: "Jo", last: nil), location: nil,
            email: nil, login: nil, dob: nil, registered: nil, phone: nil, cell: nil, id: nil, picture: nil, nat: nil
        )
    }

    private func slots(_ ids: [Int64?]) -> [CardSlot] {
        cardSlots(from: ids.map { id -> Any in id.map(user) ?? NSNull() })
    }

    @Test func placeholdersKeepTheirPosition() {
        let result = slots([42, nil, 7])

        #expect(result.map(\.id) == ["u42", "p1", "u7"])
        #expect(result[0].card?.firstName == "Jo")
        #expect(result[1].card == nil)
    }

    @Test func rangeCoversTheVisibleCards() {
        #expect(visibleIndexRange(of: slots([5, 6, 7, 8, 9]), visibleIDs: ["u6", "u8"]) == 1...3)
    }

    @Test func rangeFollowsPlaceholdersReplacedByUsers() {
        // the visible placeholders 2 and 3 became users: still positions 2...3
        #expect(visibleIndexRange(of: slots([1, 2, 3, 4]), visibleIDs: ["u3", "p9"]) == 2...2)
        #expect(visibleIndexRange(of: slots([nil, nil, nil, 4]), visibleIDs: ["p1", "p2"]) == 1...2)
    }

    @Test func noRangeWhenNothingVisible() {
        #expect(visibleIndexRange(of: slots([1, 2]), visibleIDs: []) == nil)
        #expect(visibleIndexRange(of: slots([1, 2]), visibleIDs: ["u42"]) == nil)
    }
}
