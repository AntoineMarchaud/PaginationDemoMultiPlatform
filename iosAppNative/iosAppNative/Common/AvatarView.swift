import SwiftUI

/// Round user photo with a neutral placeholder while it loads.
struct AvatarView: View {

    let url: URL?
    let size: CGFloat
    let name: String

    var body: some View {
        AsyncImage(url: url) { image in
            image.resizable().scaledToFill()
        } placeholder: {
            Image(systemName: "person.fill")
                .resizable()
                .scaledToFit()
                .padding(size * 0.24)
                .foregroundStyle(.secondary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(.fill.tertiary)
        }
        .frame(width: size, height: size)
        .clipShape(.circle)
        .accessibilityElement()
        .accessibilityLabel(Text("Photo of \(name)"))
    }
}
