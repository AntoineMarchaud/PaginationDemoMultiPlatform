import Foundation
import Testing
import Shared
@testable import paginationnative

/// Same inputs and outputs as the Kotlin UiMappersTest of the Compose app.
struct UserDisplayMapperTests {

    private func user(
        picture: PictureModel? = PictureModel(large: "l.jpg", medium: "m.jpg", thumbnail: nil),
        coordinates: CoordinatesModel? = CoordinatesModel(latitude: "48.85", longitude: "2.35"),
        dob: DobModel? = DobModel(date: nil, age: KotlinLong(value: 31))
    ) -> UserModel {
        UserModel(
            localId: 42,
            gender: nil,
            name: NameModel(title: "Mr", first: "Jo", last: "Doe"),
            location: LocationModel(
                street: StreetModel(number: KotlinLong(value: 12), name: "Rue X"),
                city: "Paris",
                state: nil,
                country: "France",
                coordinates: coordinates,
                timezone: nil
            ),
            email: "jo@doe.fr",
            login: nil,
            dob: dob,
            registered: nil,
            phone: "(01)-02-03-04",
            cell: nil,
            id: nil,
            picture: picture,
            nat: "FR"
        )
    }

    private func emptyUser(localId: Int64 = 1) -> UserModel {
        UserModel(
            localId: localId, gender: nil, name: nil, location: nil, email: nil, login: nil, dob: nil,
            registered: nil, phone: nil, cell: nil, id: nil, picture: nil, nat: nil
        )
    }

    @Test func cardMapsDisplayFields() {
        #expect(user().card == UserCard(
            localId: 42,
            imageURL: URL(string: "l.jpg"),
            email: "jo@doe.fr",
            title: "Mr",
            firstName: "Jo",
            fullName: "Jo Doe",
            age: 31,
            location: "Paris, France",
            flag: "🇫🇷"
        ))
    }

    @Test func cardSkipsMissingParts() {
        #expect(emptyUser().card == UserCard(
            localId: 1, imageURL: nil, email: "", title: "", firstName: "", fullName: "",
            age: nil, location: "", flag: ""
        ))
    }

    @Test func cardFallsBackToMediumPicture() {
        let withMediumOnly = user(picture: PictureModel(large: nil, medium: "m.jpg", thumbnail: nil))

        #expect(withMediumOnly.card.imageURL == URL(string: "m.jpg"))
    }

    @Test func cardTitleShowsFirstNameAndAge() {
        #expect(user().card.nameAndAge == "Jo, 31")
        #expect(user(dob: nil).card.nameAndAge == "Jo")
    }

    @Test func flagEmojiIgnoresInvalidCodes() {
        #expect(flagEmoji("FRA") == "")
        #expect(flagEmoji("F1") == "")
        #expect(flagEmoji(nil) == "")
        #expect(flagEmoji("us") == "🇺🇸")
    }

    @Test func detailMapsFields() {
        let detail = user().detail

        #expect(detail.card == user().card)
        #expect(detail.mainImageURL == URL(string: "l.jpg"))
        #expect(detail.address == "12 Rue X Paris")
        #expect(detail.phoneNumber == "(01)-02-03-04")
        #expect(detail.headline == "Mr · 31 years old · 🇫🇷")
    }

    @Test func detailBuildsContactURLs() {
        let detail = user().detail

        #expect(detail.emailURL == URL(string: "mailto:jo@doe.fr"))
        #expect(detail.phoneURL == URL(string: "tel:01020304"))
        // randomuser.me coordinates, streets and states are random: only the city and the country are real
        #expect(detail.mapsURL == URL(string: "https://maps.apple.com/?q=Paris%2C%20France"))
    }

    @Test func mapsURLSearchesTheEncodedCityAndCountry() {
        // street and state are random in randomuser.me: with them, maps find nothing or the wrong city
        let swiss = UserModel(
            localId: 7, gender: nil, name: nil,
            location: LocationModel(
                street: StreetModel(number: KotlinLong(value: 8036), name: "Place de L'Église"),
                city: "Neuchâtel", state: "Schwyz", country: "Switzerland", coordinates: nil, timezone: nil
            ),
            email: nil, login: nil, dob: nil, registered: nil, phone: nil, cell: nil, id: nil, picture: nil, nat: nil
        )

        #expect(swiss.detail.mapsURL == URL(string: "https://maps.apple.com/?q=Neuch%C3%A2tel%2C%20Switzerland"))
    }

    @Test func mapsURLNilWithoutCityAndCountry() {
        let streetOnly = UserModel(
            localId: 8, gender: nil, name: nil,
            location: LocationModel(
                street: StreetModel(number: KotlinLong(value: 12), name: "Rue X"),
                city: nil, state: "Schwyz", country: nil, coordinates: nil, timezone: nil
            ),
            email: nil, login: nil, dob: nil, registered: nil, phone: nil, cell: nil, id: nil, picture: nil, nat: nil
        )

        #expect(streetOnly.detail.mapsURL == nil)
    }

    @Test func detailHandlesMissingFields() {
        let detail = emptyUser().detail

        #expect(detail.address == "")
        #expect(detail.phoneNumber == "")
        #expect(detail.birthday == "")
        #expect(detail.headline == "")
        #expect(detail.emailURL == nil)
        #expect(detail.phoneURL == nil)
        #expect(detail.mapsURL == nil)
    }

    @Test func invalidCoordinatesDoNotCrash() {
        let detail = user(coordinates: CoordinatesModel(latitude: "abc", longitude: "2.35")).detail

        #expect(detail.mapsURL == user().detail.mapsURL)
    }

    @Test func birthdayEmptyWhenDobMissing() {
        #expect(user(dob: nil).detail.birthday == "")
    }

    @Test func birthdayIsFormattedAsCalendarDate() {
        // domain dates come from LocalDateTime.toString(), without zone
        let born = user(dob: DobModel(date: "1993-04-23T23:34:12.674", age: KotlinLong(value: 31)))

        #expect(born.detail.birthday == "April 23, 1993")
    }

    @Test func invalidBirthdayIsEmpty() {
        #expect(user(dob: DobModel(date: "oops", age: nil)).detail.birthday == "")
        #expect(user(dob: DobModel(date: "1993-02-30T10:00", age: nil)).detail.birthday == "")
    }
}
