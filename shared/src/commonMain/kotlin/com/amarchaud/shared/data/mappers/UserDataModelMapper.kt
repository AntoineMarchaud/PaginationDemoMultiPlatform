package com.amarchaud.shared.data.mappers

import com.amarchaud.shared.data.models.CoordinatesDataModel
import com.amarchaud.shared.data.models.CoordinatesEntityModel
import com.amarchaud.shared.data.models.DobDataModel
import com.amarchaud.shared.data.models.DobEntityModel
import com.amarchaud.shared.data.models.IdDataModel
import com.amarchaud.shared.data.models.IdEntityModel
import com.amarchaud.shared.data.models.LocationDataModel
import com.amarchaud.shared.data.models.LocationEntityModel
import com.amarchaud.shared.data.models.LoginDataModel
import com.amarchaud.shared.data.models.NameDataModel
import com.amarchaud.shared.data.models.NameEntityModel
import com.amarchaud.shared.data.models.PictureDataModel
import com.amarchaud.shared.data.models.PictureEntityModel
import com.amarchaud.shared.data.models.RegisteredDataModel
import com.amarchaud.shared.data.models.RegisteredEntityModel
import com.amarchaud.shared.data.models.StreetDataModel
import com.amarchaud.shared.data.models.StreetEntityModel
import com.amarchaud.shared.data.models.TimezoneDataModel
import com.amarchaud.shared.data.models.TimezoneEntityModel
import com.amarchaud.shared.data.models.UserDataModel
import com.amarchaud.shared.data.models.UserEntityModel
import com.amarchaud.shared.domain.models.CoordinatesModel
import com.amarchaud.shared.domain.models.DobModel
import com.amarchaud.shared.domain.models.IdModel
import com.amarchaud.shared.domain.models.LocationModel
import com.amarchaud.shared.domain.models.LoginModel
import com.amarchaud.shared.domain.models.NameModel
import com.amarchaud.shared.domain.models.PictureModel
import com.amarchaud.shared.domain.models.RegisteredModel
import com.amarchaud.shared.domain.models.StreetModel
import com.amarchaud.shared.domain.models.TimezoneModel
import com.amarchaud.shared.domain.models.UserModel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// DataModel to domain

internal fun UserDataModel.toDomain() = UserModel(
    localId = -1, // only when passing from entity to domain
    gender = this.gender,
    name = this.name?.toDomain(),
    location = this.location?.toDomain(),
    email = this.email,
    login = this.login?.toDomain(),
    dob = this.dob?.toDomain(),
    registered = this.registered?.toDomain(),
    phone = this.phone,
    cell = this.cell,
    id = this.id?.toDomain(),
    picture = this.picture?.toDomain(),
    nat = this.nat,
)

internal fun NameDataModel.toDomain() = NameModel(
    title = this.title,
    first = this.first,
    last = this.last
)

internal fun LocationDataModel.toDomain() = LocationModel(
    street = this.street?.toDomain(),
    city = this.city,
    state = this.state,
    country = this.country,
    /*
    postcode = when(this.postcode) {
        is Int -> this.postcode.toString()
        is String -> this.postcode
        else -> throw Exception("impossible")
    },*/
    coordinates = this.coordinates?.toDomain(),
    timezone = this.timezone?.toDomain(),
)

internal fun StreetDataModel.toDomain() = StreetModel(
    number = this.number,
    name = this.name,
)

internal fun CoordinatesDataModel.toDomain() = CoordinatesModel(
    latitude = this.latitude,
    longitude = this.longitude,
)

internal fun TimezoneDataModel.toDomain() = TimezoneModel(
    offset = this.offset,
    description = this.description,
)

internal fun LoginDataModel.toDomain() = LoginModel(
    uuid = this.uuid,
    username = this.username,
    password = this.password,
    salt = this.salt,
    md5 = this.md5,
    sha1 = this.sha1,
    sha256 = this.sha256,
)

internal fun DobDataModel.toDomain() = DobModel(
    date = this.date?.toString(),
    age = this.age,
)

internal fun RegisteredDataModel.toDomain() = RegisteredModel(
    date = this.date?.toString(),
    age = this.age,
)

internal fun IdDataModel.toDomain() = IdModel(
    name = this.name,
    value = this.value
)

internal fun PictureDataModel.toDomain() = PictureModel(
    large = this.large,
    medium = this.medium,
    thumbnail = this.thumbnail,
)


// DataModel to EntityModel

internal fun UserDataModel.toEntity() = UserEntityModel(
    gender = this.gender,
    name = this.name?.toEntity(),
    location = this.location?.toEntity(),
    email = this.email.orEmpty(),
    dob = this.dob?.toEntity(),
    registered = this.registered?.toEntity(),
    phone = this.phone,
    cell = this.cell,
    id = this.id?.toEntity(),
    picture = this.picture?.toEntity(),
    nat = this.nat,
)

internal fun NameDataModel.toEntity() = NameEntityModel(
    title = this.title,
    first = this.first,
    last = this.last
)

internal fun LocationDataModel.toEntity() = LocationEntityModel(
    street = this.street?.toEntity(),
    city = this.city,
    state = this.state,
    country = this.country,
    /*
    postcode = when(this.postcode) {
        is Int -> this.postcode.toString()
        is String -> this.postcode
        else -> throw Exception("impossible")
    },*/
    coordinates = this.coordinates?.toEntity(),
    timezone = this.timezone?.toEntity(),
)

internal fun StreetDataModel.toEntity() = StreetEntityModel(
    number = this.number,
    name = this.name,
)

internal fun CoordinatesDataModel.toEntity() = CoordinatesEntityModel(
    latitude = this.latitude,
    longitude = this.longitude,
)

internal fun TimezoneDataModel.toEntity() = TimezoneEntityModel(
    offset = this.offset,
    description = this.description,
)


internal fun DobDataModel.toEntity() = DobEntityModel(
    date = this.date?.toLocalDate(), // todo
    age = this.age,
)

internal fun RegisteredDataModel.toEntity() = RegisteredEntityModel(
    date = this.date?.toLocalDate(), // todo
    age = this.age,
)

internal fun IdDataModel.toEntity() = IdEntityModel(
    name = this.name,
    value = this.value
)

internal fun PictureDataModel.toEntity() = PictureEntityModel(
    large = this.large,
    medium = this.medium,
    thumbnail = this.thumbnail,
)

private fun String.toLocalDate(): LocalDateTime {
    // Convertir une chaîne ISO 8601 avec "Z" en LocalDateTime
    return try {
        Instant.parse(this).toLocalDateTime(TimeZone.UTC)
    } catch (e: Exception) {
        Clock.System.now().toLocalDateTime(TimeZone.UTC)
    }
}