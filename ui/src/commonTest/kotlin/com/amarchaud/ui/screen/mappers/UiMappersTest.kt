package com.amarchaud.ui.screen.mappers

import com.amarchaud.domain.models.CoordinatesModel
import com.amarchaud.domain.models.DobModel
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.models.LocationModel
import com.amarchaud.domain.models.NameModel
import com.amarchaud.domain.models.PictureModel
import com.amarchaud.domain.models.StreetModel
import com.amarchaud.domain.models.UserModel
import com.amarchaud.ui.screen.detail.mappers.toDetailUiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import com.amarchaud.ui.screen.mainList.models.UserGenericUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UiMappersTest {

    private val user = UserModel(
        localId = 42,
        name = NameModel(title = "Mr", first = "Jo", last = "Doe"),
        email = "jo@doe.fr",
        picture = PictureModel(large = "l.jpg", medium = "m.jpg"),
        location = LocationModel(
            street = StreetModel(number = 12, name = "Rue X"),
            city = "Paris",
            country = "France",
            coordinates = CoordinatesModel(latitude = "48.85", longitude = "2.35"),
        ),
        phone = "(01)-02-03-04",
        dob = DobModel(age = 31),
        nat = "FR",
    )

    @Test
    fun genericUiModelMapsDisplayFields() {
        assertEquals(
            UserGenericUiModel(
                localId = 42,
                imageUrl = "l.jpg",
                email = "jo@doe.fr",
                title = "Mr",
                firstName = "Jo",
                fullName = "Jo Doe",
                age = 31,
                location = "Paris, France",
                flag = "🇫🇷",
            ),
            user.toGenericUiModel(),
        )
    }

    @Test
    fun genericUiModelSkipsMissingParts() {
        assertEquals(UserGenericUiModel(localId = 1), UserModel(localId = 1).toGenericUiModel())
    }

    @Test
    fun genericUiModelFallsBackToMediumPicture() {
        val withMediumOnly = user.copy(picture = PictureModel(medium = "m.jpg"))

        assertEquals("m.jpg", withMediumOnly.toGenericUiModel().imageUrl)
    }

    @Test
    fun flagEmojiIgnoresInvalidCodes() {
        assertEquals("", "FRA".toFlagEmoji())
        assertEquals("", "F1".toFlagEmoji())
        assertEquals("", null.toFlagEmoji())
        assertEquals("🇺🇸", "us".toFlagEmoji())
    }

    @Test
    fun errorUiModelMapsEveryCase() {
        val withCode = ErrorApiModel.ApiServerErrorWithCode(503, "unavailable").toErrorUiModel()
        assertIs<ErrorApiUiModel.ApiServerErrorWithCode>(withCode)
        assertEquals(503, withCode.responseCode)
        assertEquals("unavailable", withCode.responseMessage)

        assertEquals(ErrorApiUiModel.ApiNullBody, ErrorApiModel.ApiNullBody().toErrorUiModel())
        assertEquals(ErrorApiUiModel.ApiGenericServerError, ErrorApiModel.ApiGenericServerError().toErrorUiModel())
        assertEquals(ErrorApiUiModel.SocketTimeOutError, ErrorApiModel.SocketTimeOutError().toErrorUiModel())
        assertEquals(ErrorApiUiModel.NoInternetError, ErrorApiModel.NoInternetError().toErrorUiModel())
        assertEquals(ErrorApiUiModel.GenericError, ErrorApiModel.GenericError().toErrorUiModel())
    }

    @Test
    fun detailUiModelMapsFields() {
        val detail = user.toDetailUiModel()

        assertEquals(user.toGenericUiModel(), detail.mainInfo)
        assertEquals("l.jpg", detail.mainImageUrl)
        assertEquals(48.85 to 2.35, detail.coordinates)
        assertEquals("12 Rue X Paris", detail.address)
        assertEquals("(01)-02-03-04", detail.phoneNumber)
    }

    @Test
    fun detailUiModelBuildsContactUris() {
        val detail = user.toDetailUiModel()

        assertEquals("mailto:jo@doe.fr", detail.emailUri)
        assertEquals("tel:01020304", detail.phoneUri)
        assertEquals("https://www.google.com/maps/search/?api=1&query=48.85,2.35", detail.mapsUri)
    }

    @Test
    fun detailUiModelHandlesMissingFields() {
        val detail = UserModel(localId = 1).toDetailUiModel()

        assertEquals("", detail.address)
        assertEquals("", detail.phoneNumber)
        assertEquals("", detail.birthday)
        assertEquals("", detail.emailUri)
        assertEquals("", detail.phoneUri)
        assertEquals("", detail.mapsUri)
        assertEquals(0.0 to 0.0, detail.coordinates)
    }

    @Test
    fun invalidCoordinatesDoNotCrash() {
        val invalid = user.copy(location = user.location?.copy(coordinates = CoordinatesModel("abc", "2.35")))

        val detail = invalid.toDetailUiModel()

        assertEquals(0.0 to 0.0, detail.coordinates)
        assertEquals("", detail.mapsUri)
    }

    @Test
    fun birthdayEmptyWhenDobMissing() {
        assertEquals("", user.copy(dob = null).toDetailUiModel().birthday)
    }

    @Test
    fun birthdayIsFormattedAsCalendarDate() {
        // domain dates come from LocalDateTime.toString(), without zone
        val born = user.copy(dob = DobModel(date = "1993-04-23T23:34:12.674", age = 31))

        assertEquals("April 23, 1993", born.toDetailUiModel().birthday)
    }

    @Test
    fun invalidBirthdayIsEmpty() {
        assertEquals("", user.copy(dob = DobModel(date = "oops")).toDetailUiModel().birthday)
    }
}
