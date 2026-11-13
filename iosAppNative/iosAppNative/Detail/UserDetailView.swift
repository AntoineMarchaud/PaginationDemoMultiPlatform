import SwiftUI

/// A person's detail: blurred photo header, glass quick actions, contact and about sections.
/// The content is centered and width-limited on tablets, foldables and landscape.
struct UserDetailView: View {

    @State private var viewModel: UserDetailViewModel
    let zoom: Namespace.ID

    init(localId: Int64, zoom: Namespace.ID) {
        _viewModel = State(initialValue: UserDetailViewModel(localId: localId))
        self.zoom = zoom
    }

    var body: some View {
        ScrollView {
            if let user = viewModel.user {
                VStack(spacing: 24) {
                    header(user)
                    VStack(spacing: 28) {
                        QuickActionsView(user: user)
                        InfoSectionView(title: "Contact", rows: [
                            InfoRow(label: "Email", systemImage: "envelope", value: user.card.email, url: user.emailURL),
                            InfoRow(label: "Phone", systemImage: "phone", value: user.phoneNumber, url: user.phoneURL),
                        ])
                        InfoSectionView(title: "About", rows: [
                            InfoRow(label: "Address", systemImage: "mappin.and.ellipse", value: user.address, url: user.mapsURL),
                            InfoRow(label: "Birthday", systemImage: "birthday.cake", value: user.birthday),
                        ])
                    }
                    .padding(.horizontal, 16)
                    .frame(maxWidth: 640)
                }
                .padding(.bottom, 32)
            }
        }
        .background(Color(.systemGroupedBackground))
        .ignoresSafeArea(edges: .top)
        .navigationBarTitleDisplayMode(.inline)
        .navigationTransition(.zoom(sourceID: viewModel.localId, in: zoom))
        .task { await viewModel.load() }
    }

    private func header(_ user: UserDetail) -> some View {
        VStack(spacing: 8) {
            AvatarView(url: user.mainImageURL, size: 128, name: user.card.fullName)
                .overlay(Circle().stroke(.background, lineWidth: 4))
                .shadow(color: .black.opacity(0.2), radius: 12, y: 6)
                .padding(.bottom, 8)
            Text(user.card.fullName)
                .font(.title.bold())
                .multilineTextAlignment(.center)
            if !user.headline.isEmpty {
                Text(user.headline)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            if !user.card.location.isEmpty {
                Label(user.card.location, systemImage: "location.fill")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 120)
        .padding(.bottom, 8)
        .frame(maxWidth: .infinity)
        .background(alignment: .top) {
            // blurred photo fading into the page background
            AsyncImage(url: user.mainImageURL) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                Color.clear
            }
            .frame(height: 300)
            .frame(maxWidth: .infinity)
            .blur(radius: 40)
            .opacity(0.7)
            .clipped()
            .overlay {
                LinearGradient(colors: [.clear, Color(.systemGroupedBackground)], startPoint: .center, endPoint: .bottom)
            }
        }
    }
}
