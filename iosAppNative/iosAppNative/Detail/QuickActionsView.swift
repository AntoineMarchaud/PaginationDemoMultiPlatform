import SwiftUI

/// Email / Call / Map glass buttons, disabled when the information is missing.
struct QuickActionsView: View {

    let user: UserDetail
    @Environment(\.openURL) private var openURL

    var body: some View {
        GlassEffectContainer(spacing: 12) {
            HStack(spacing: 12) {
                action("Email", systemImage: "envelope.fill", url: user.emailURL)
                action("Call", systemImage: "phone.fill", url: user.phoneURL)
                action("Map", systemImage: "map.fill", url: user.mapsURL)
            }
        }
    }

    private func action(_ title: LocalizedStringKey, systemImage: String, url: URL?) -> some View {
        Button {
            if let url { openURL(url) }
        } label: {
            VStack(spacing: 6) {
                Image(systemName: systemImage)
                    .font(.title3)
                Text(title)
                    .font(.caption.weight(.semibold))
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 8)
        }
        .buttonStyle(.glass)
        .disabled(url == nil)
    }
}
