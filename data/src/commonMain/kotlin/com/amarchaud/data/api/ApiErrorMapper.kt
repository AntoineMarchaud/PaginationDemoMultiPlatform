package com.amarchaud.data.api

import com.amarchaud.data.models.ErrorApiDataModel
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException

/**
 * Turns Ktor / network exceptions into typed API errors.
 * Anything else (cancellation, parsing error, ...) is returned unchanged.
 */
internal fun Throwable.toErrorApiDataModel(): Throwable = when (this) {
    is ResponseException -> ErrorApiDataModel.ApiServerErrorWithCode(
        responseCode = response.status.value,
        responseMessage = response.status.description,
    )
    // timeouts are IOExceptions too: check them first
    is HttpRequestTimeoutException,
    is ConnectTimeoutException,
    is SocketTimeoutException -> ErrorApiDataModel.SocketTimeOutError()
    is IOException -> ErrorApiDataModel.NoInternetError()
    else -> this
}
