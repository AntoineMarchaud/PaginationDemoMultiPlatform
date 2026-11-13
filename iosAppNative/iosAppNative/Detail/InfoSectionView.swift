import SwiftUI

struct InfoRow: Identifiable {
    let label: LocalizedStringKey
    let systemImage: String
    let value: String
    var url: URL?

    var id: String { systemImage }
}

/// Titled group of label / value rows, like the system grouped lists. Rows without a value are skipped.
struct InfoSectionView: View {

    let title: LocalizedStringKey
    let rows: [InfoRow]
    @Environment(\.openURL) private var openURL

    var body: some View {
        let visibleRows = rows.filter { !$0.value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty }
        if !visibleRows.isEmpty {
            VStack(alignment: .leading, spacing: 8) {
                Text(title)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.secondary)
                    .padding(.leading, 20)
                VStack(spacing: 0) {
                    ForEach(visibleRows) { row in
                        rowView(row)
                        if row.id != visibleRows.last?.id {
                            Divider().padding(.leading, 64)
                        }
                    }
                }
                .background(Color(.secondarySystemGroupedBackground), in: .rect(cornerRadius: 26))
            }
        }
    }

    @ViewBuilder
    private func rowView(_ row: InfoRow) -> some View {
        let content = HStack(spacing: 14) {
            Image(systemName: row.systemImage)
                .font(.body.weight(.semibold))
                .foregroundStyle(.tint)
                .frame(width: 34, height: 34)
                .background(.tint.opacity(0.14), in: .rect(cornerRadius: 10))
            VStack(alignment: .leading, spacing: 2) {
                Text(row.label)
                    .font(.caption)
                    .foregroundStyle(.secondary)
                Text(row.value)
                    .font(.body)
                    .foregroundStyle(.primary)
            }
            Spacer(minLength: 0)
            if row.url != nil {
                Image(systemName: "chevron.right")
                    .font(.footnote.weight(.semibold))
                    .foregroundStyle(.tertiary)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .contentShape(.rect)

        if let url = row.url {
            Button { openURL(url) } label: { content }
                .buttonStyle(.plain)
        } else {
            content
        }
    }
}
