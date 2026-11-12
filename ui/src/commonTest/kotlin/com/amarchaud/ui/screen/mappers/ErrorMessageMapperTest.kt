package com.amarchaud.ui.screen.mappers

import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import paginationdemomultiplatform.ui.generated.resources.Res
import paginationdemomultiplatform.ui.generated.resources.error_ApiServerErrorWithCode
import paginationdemomultiplatform.ui.generated.resources.error_GenericError
import paginationdemomultiplatform.ui.generated.resources.error_NoInternetError
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorMessageMapperTest {

    @Test
    fun domainErrorThrowableIsMappedToUiModel() {
        val error: Throwable = ErrorApiModel.NoInternetError()

        assertEquals(ErrorApiUiModel.NoInternetError, error.toErrorUiModel())
    }

    @Test
    fun unknownThrowableBecomesGenericError() {
        assertEquals(ErrorApiUiModel.GenericError, IllegalStateException("boom").toErrorUiModel())
    }

    @Test
    fun serverErrorMessageCarriesTheCode() {
        assertEquals(
            ErrorMessage(Res.string.error_ApiServerErrorWithCode, listOf(503)),
            ErrorApiUiModel.ApiServerErrorWithCode(503, "Service Unavailable").toErrorMessage(),
        )
    }

    @Test
    fun noInternetHasItsOwnMessage() {
        assertEquals(ErrorMessage(Res.string.error_NoInternetError), ErrorApiUiModel.NoInternetError.toErrorMessage())
    }

    @Test
    fun genericErrorHasItsOwnMessage() {
        assertEquals(ErrorMessage(Res.string.error_GenericError), ErrorApiUiModel.GenericError.toErrorMessage())
    }
}
