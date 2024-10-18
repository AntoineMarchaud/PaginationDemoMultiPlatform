package com.amarchaud.shared.data.mappers

import com.amarchaud.shared.data.models.CoordinatesEntityModel
import com.amarchaud.shared.data.models.DobEntityModel
import com.amarchaud.shared.data.models.IdEntityModel
import com.amarchaud.shared.data.models.LocationEntityModel
import com.amarchaud.shared.data.models.NameEntityModel
import com.amarchaud.shared.data.models.PictureEntityModel
import com.amarchaud.shared.data.models.RegisteredEntityModel
import com.amarchaud.shared.data.models.StreetEntityModel
import com.amarchaud.shared.data.models.TimezoneEntityModel
import com.amarchaud.shared.data.models.UserEntityModel
import com.amarchaud.shared.domain.models.CoordinatesModel
import com.amarchaud.shared.domain.models.DobModel
import com.amarchaud.shared.domain.models.IdModel
import com.amarchaud.shared.domain.models.LocationModel
import com.amarchaud.shared.domain.models.NameModel
import com.amarchaud.shared.domain.models.PictureModel
import com.amarchaud.shared.domain.models.RegisteredModel
import com.amarchaud.shared.domain.models.StreetModel
import com.amarchaud.shared.domain.models.TimezoneModel
import com.amarchaud.shared.domain.models.UserModel

internal fun UserEntityModel.toDomain() = UserModel(
    localId = this._id,
    gender = this.gender,
    name = this.name?.toDomain(),
    location = this.location?.toDomain(),
    email = this.email,
    dob = this.dob?.toDomain(),
    registered = this.registered?.toDomain(),
    phone = this.phone,
    cell = this.cell,
    id = this.id?.toDomain(),
    picture = this.picture?.toDomain(),
    nat = this.nat,
)

internal fun NameEntityModel.toDomain() = NameModel(
    title = this.title,
    first = this.first,
    last = this.last
)

internal fun LocationEntityModel.toDomain() = LocationModel(
    street = this.street?.toDomain(),
    city = this.city,
    state = this.state,
    country = this.country,
    //postcode = this.postcode,
    coordinates = this.coordinates?.toDomain(),
    timezone = this.timezone?.toDomain(),
)

internal fun StreetEntityModel.toDomain() = StreetModel(
    number = this.number,
    name = this.name,
)

internal fun CoordinatesEntityModel.toDomain() = CoordinatesModel(
    latitude = this.latitude,
    longitude = this.longitude,
)

internal fun TimezoneEntityModel.toDomain() = TimezoneModel(
    offset = this.offset,
    description = this.description,
)

internal fun DobEntityModel.toDomain() = DobModel(
    date = this.date?.toString(),
    age = this.age,
)

internal fun RegisteredEntityModel.toDomain() = RegisteredModel(
    date = this.date?.toString(),
    age = this.age,
)

internal fun IdEntityModel.toDomain() = IdModel(
    name = this.name,
    value = this.value
)

internal fun PictureEntityModel.toDomain() = PictureModel(
    large = this.large,
    medium = this.medium,
    thumbnail = this.thumbnail,
)