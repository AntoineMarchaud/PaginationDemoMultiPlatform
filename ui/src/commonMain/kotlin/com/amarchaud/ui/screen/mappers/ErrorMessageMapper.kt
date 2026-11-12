package com.amarchaud.ui.screen.mappers

import androidx.compose.runtime.Composable
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import paginationdemomultiplatform.ui.generated.resources.Res
import paginationdemomultiplatform.ui.generated.resources.error_ApiGenericServerError
import paginationdemomultiplatform.ui.generated.resources.error_ApiNullBody
import paginationdemomultiplatform.ui.generated.resources.error_ApiServerErrorWithCode
import paginationdemomultiplatform.ui.generated.resources.error_GenericError
import paginationdemomultiplatform.ui.generated.resources.error_NoInternetError
import paginationdemomultiplatform.ui.generated.resources.error_SocketTimeOutError

/** A localized message: the string resource and its format arguments. */
internal data class ErrorMessage(
    val resource: StringResource,
    val args: List<Any> = emptyList(),
)

/** Errors coming from Paging's `LoadState.Error` are plain throwables. */
internal fun Throwable.toErrorUiModel(): ErrorApiUiModel =
    (this as? ErrorApiModel)?.toErrorUiModel() ?: ErrorApiUiModel.GenericError

internal fun ErrorApiUiModel.toErrorMessage(): ErrorMessage = when (this) {
    is ErrorApiUiModel.ApiServerErrorWithCode -> ErrorMessage(Res.string.error_ApiServerErrorWithCode, listOf(responseCode))
    ErrorApiUiModel.ApiNullBody -> ErrorMessage(Res.string.error_ApiNullBody)
    ErrorApiUiModel.ApiGenericServerError -> ErrorMessage(Res.string.error_ApiGenericServerError)
    ErrorApiUiModel.SocketTimeOutError -> ErrorMessage(Res.string.error_SocketTimeOutError)
    ErrorApiUiModel.NoInternetError -> ErrorMessage(Res.string.error_NoInternetError)
    ErrorApiUiModel.GenericError -> ErrorMessage(Res.string.error_GenericError)
}

@Composable
internal fun Throwable.errorMessage(): String {
    val message = toErrorUiModel().toErrorMessage()
    return stringResource(message.resource, *message.args.toTypedArray())
}
