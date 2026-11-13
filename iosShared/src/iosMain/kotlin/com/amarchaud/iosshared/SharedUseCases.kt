package com.amarchaud.iosshared

import com.amarchaud.domain.usecase.GetOneUserUseCase
import com.amarchaud.domain.usecase.GetRandomUsersUseCase
import org.koin.mp.KoinPlatform

/** The domain use cases for Swift, resolved from Koin. Call [initShared] first. */
class SharedUseCases {
    val getRandomUsers: GetRandomUsersUseCase = KoinPlatform.getKoin().get()
    val getOneUser: GetOneUserUseCase = KoinPlatform.getKoin().get()
}
