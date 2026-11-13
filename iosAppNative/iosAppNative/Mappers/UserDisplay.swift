import Foundation
import Shared

/// One card of the Discover grid. Same rules as the Compose app's UserGenericUiModel.
struct UserCard: Identifiable, Equatable {
    let localId: Int64
    let imageURL: URL?
    let email: String
    let title: String
    let firstName: String
    /// First and last name, without title.
    let fullName: String
    let age: Int?
    /// "City, Country"
    let location: String
    /// Country flag emoji built from the nationality code, empty if unknown.
    let flag: String

    var id: Int64 { localId }

    /// "Jo, 31", or "Jo" when the age is unknown.
    var nameAndAge: String {
        guard let age else { return firstName }
        return String(localized: "\(firstName), \(age)")
    }

    /// Fake content drawn redacted while the first page loads.
    static func placeholder(_ index: Int) -> UserCard {
        UserCard(
            localId: -Int64(index) - 1, imageURL: nil, email: "", title: "", firstName: "Firstname",
            fullName: "Firstname Lastname", age: 30, location: "City, Country", flag: ""
        )
    }
}

/// The detail screen. Same rules as the Compose app's UserDetailUiModel.
struct UserDetail: Equatable {
    let card: UserCard
    let mainImageURL: URL?
    let address: String
    let phoneNumber: String
    let birthday: String
    /// "Mr · 31 years old · 🇫🇷"
    let headline: String
    // ready-to-open URLs, nil when the information is missing
    let emailURL: URL?
    let phoneURL: URL?
    let mapsURL: URL?
}

extension UserModel {

    var card: UserCard {
        UserCard(
            localId: localId,
            // randomuser.me pictures are 128px max: always take the largest one
            imageURL: URL(string: picture?.large ?? picture?.medium ?? ""),
            email: email ?? "",
            title: name?.title ?? "",
            firstName: name?.first ?? "",
            fullName: joinNonBlank(" ", name?.first, name?.last),
            age: dob?.age.map { Int($0.int64Value) },
            location: joinNonBlank(", ", location?.city, location?.country),
            flag: flagEmoji(nat)
        )
    }

    var detail: UserDetail {
        let card = self.card
        // randomuser.me coordinates, streets and states are random (a street of Lviv in the Kyiv region...):
        // only the city and the country are real, anything else makes maps find nothing or the wrong city
        let mapsQuery = joinNonBlank(", ", location?.city, location?.country)
        let phoneDigits = (phone ?? "").filter { ("0"..."9").contains($0) || $0 == "+" }
        let email = self.email ?? ""

        return UserDetail(
            card: card,
            mainImageURL: card.imageURL,
            address: joinNonBlank(" ", location?.street?.number?.stringValue, location?.street?.name, location?.city),
            phoneNumber: phone ?? "",
            birthday: (dob?.date).map(calendarDate) ?? "",
            headline: joinNonBlank(" · ", card.title, card.age.map { String(localized: "\($0) years old") }, card.flag),
            emailURL: email.isBlank ? nil : URL(string: "mailto:\(email)"),
            phoneURL: phoneDigits.isEmpty ? nil : URL(string: "tel:\(phoneDigits)"),
            // native app: Apple Maps instead of the Google Maps link of the Compose app
            mapsURL: mapsQuery.isEmpty ? nil : URL(string: "https://maps.apple.com/?q=\(mapsQuery.urlQueryEncoded)")
        )
    }
}

/// "FR" -> 🇫🇷 (two regional indicator symbols), "" when the code is not two letters.
func flagEmoji(_ code: String?) -> String {
    guard let code = code?.uppercased(), code.unicodeScalars.count == 2,
          code.unicodeScalars.allSatisfy({ ("A"..."Z").contains($0) }) else { return "" }
    let indicators = code.unicodeScalars.compactMap { Unicode.Scalar(0x1F1E6 + $0.value - 0x41) }
    return String(String.UnicodeScalarView(indicators))
}

func joinNonBlank(_ separator: String, _ parts: String?...) -> String {
    parts.compactMap { $0 }.filter { !$0.isBlank }.joined(separator: separator)
}

/// "1993-04-23T23:34:12.674" -> "April 23, 1993", "" when invalid.
/// A birth date is a calendar date: no time zone conversion, otherwise the day could shift.
private func calendarDate(_ isoLocalDateTime: String) -> String {
    let pattern = /^(\d{4})-(\d{2})-(\d{2})T\d{2}:\d{2}(:\d{2}(\.\d+)?)?$/
    guard let match = isoLocalDateTime.wholeMatch(of: pattern),
          let year = Int(match.1), let month = Int(match.2), let day = Int(match.3) else { return "" }
    var calendar = Calendar(identifier: .gregorian)
    calendar.timeZone = TimeZone(identifier: "UTC")!
    guard DateComponents(calendar: calendar, year: year, month: month, day: day).isValidDate else { return "" }
    // English month name like the Compose app, whatever the device language
    let monthName = DateFormatter.englishMonths[month - 1]
    return "\(monthName) \(day), \(year)"
}

private extension DateFormatter {
    static let englishMonths: [String] = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        return formatter.monthSymbols
    }()
}

private extension String {
    var isBlank: Bool { trimmingCharacters(in: .whitespacesAndNewlines).isEmpty }

    /// Percent-encodes (UTF-8) everything but the URL unreserved characters: `A-Z a-z 0-9 - . _ ~`.
    var urlQueryEncoded: String {
        addingPercentEncoding(withAllowedCharacters: .urlUnreserved) ?? ""
    }
}

private extension CharacterSet {
    static let urlUnreserved = CharacterSet(charactersIn: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~")
}
