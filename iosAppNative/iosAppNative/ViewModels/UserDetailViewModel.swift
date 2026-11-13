import Foundation
import Observation
import Shared

/// Detail screen: one cached user of the shared domain (GetOneUserUseCase).
@MainActor
@Observable
final class UserDetailViewModel {

    let localId: Int64
    private(set) var user: UserDetail?

    @ObservationIgnored private let useCases: SharedUseCases

    init(localId: Int64, useCases: SharedUseCases = SharedUseCases()) {
        self.localId = localId
        self.useCases = useCases
    }

    func load() async {
        guard user == nil else { return }
        // Kotlin suspend function, exported as async
        user = try? await useCases.getOneUser.run(localId: localId)?.detail
    }
}
