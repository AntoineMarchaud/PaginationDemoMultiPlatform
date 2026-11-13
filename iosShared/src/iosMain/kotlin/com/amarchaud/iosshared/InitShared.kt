package com.amarchaud.iosshared

import com.amarchaud.data.di.daoCommonModule
import com.amarchaud.data.di.daoModule
import com.amarchaud.data.di.networkModule
import com.amarchaud.data.di.repoModule
import com.amarchaud.data.di.useCaseModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

/** Called once by the SwiftUI app at launch: data and domain only (no settings, no ViewModels). */
fun initShared() {
    if (KoinPlatform.getKoinOrNull() != null) return
    startKoin {
        modules(networkModule, daoModule, daoCommonModule, repoModule, useCaseModule)
    }
}
