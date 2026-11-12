package com.amarchaud.data.di

import com.amarchaud.data.api.PaginationDemoApi
import com.amarchaud.data.api.PaginationDemoApiKtorImpl
import com.amarchaud.data.api.configurePaginationDemo
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import org.koin.core.module.Module
import org.koin.dsl.module

actual val networkModule: Module = module {

    // Provide the Ktor HttpClient
    single<HttpClient> {
        HttpClient(Android) {
            configurePaginationDemo()
        }
    }

    // Provide the API implementation
    single<PaginationDemoApi> {
        PaginationDemoApiKtorImpl(get())
    }
}
