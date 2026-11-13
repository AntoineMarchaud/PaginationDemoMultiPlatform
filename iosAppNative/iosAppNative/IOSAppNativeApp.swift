import SwiftUI
import Shared

@main
struct IOSAppNativeApp: App {

    init() {
        // Koin: data + domain from the Kotlin Shared framework
        InitSharedKt.doInitShared()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
