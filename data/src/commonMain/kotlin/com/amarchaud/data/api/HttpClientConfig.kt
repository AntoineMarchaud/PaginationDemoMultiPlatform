package com.amarchaud.data.api

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Configuration shared by the Android and iOS (Darwin) clients.
 */
internal fun HttpClientConfig<*>.configurePaginationDemo() {
    // any non-2xx response becomes a failure instead of being parsed as a valid page
    expectSuccess = true
    install(DefaultRequest) {
        url("https://randomuser.me/")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
    install(ContentNegotiation) {
        json(Json)
    }
}
