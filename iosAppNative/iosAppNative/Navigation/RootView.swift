import SwiftUI

/// Native navigation: Discover grid, then a user's detail (zoom transition from its card).
struct RootView: View {

    @State private var path: [Int64] = []
    @State private var listViewModel = MainListViewModel()
    @Namespace private var zoom

    var body: some View {
        NavigationStack(path: $path) {
            DiscoverView(viewModel: listViewModel, zoom: zoom)
                .navigationDestination(for: Int64.self) { localId in
                    UserDetailView(localId: localId, zoom: zoom)
                }
        }
    }
}
