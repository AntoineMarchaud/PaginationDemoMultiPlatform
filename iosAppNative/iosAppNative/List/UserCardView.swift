import SwiftUI

/// One person of the Discover grid.
struct UserCardView: View {

    let card: UserCard

    var body: some View {
        VStack(spacing: 14) {
            AvatarView(url: card.imageURL, size: 84, name: card.fullName)
            VStack(spacing: 4) {
                Text(card.nameAndAge)
                    .font(.headline)
                    .foregroundStyle(.primary)
                Text(card.location)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            .lineLimit(1)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 22)
        .padding(.horizontal, 12)
        .background(Color(.secondarySystemGroupedBackground), in: .rect(cornerRadius: 26))
        .overlay(alignment: .topTrailing) {
            Text(card.flag)
                .font(.title3)
                .padding(12)
                .accessibilityHidden(true)
        }
        .contentShape(.rect(cornerRadius: 26))
    }
}
