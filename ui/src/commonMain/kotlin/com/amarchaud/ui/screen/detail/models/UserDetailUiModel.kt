package com.amarchaud.ui.screen.detail.models

import com.amarchaud.ui.screen.mainList.models.UserGenericUiModel

data class UserDetailUiModel(
    // same as UserUiModel
    val mainInfo: UserGenericUiModel = UserGenericUiModel(),
    // plus a lot of info
    val mainImageUrl: String = "",
    val coordinates: Pair<Double, Double> = Pair(0.0, 0.0),
    val address: String = "",
    val phoneNumber: String = "",
    val birthday: String = ""
)