package com.amarchaud.data.mappers

import com.amarchaud.data.models.ErrorApiDataModel
import com.amarchaud.domain.models.ErrorApiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ErrorApiDataModelMapperTest {

    @Test
    fun serverErrorWithCodeKeepsCodeAndMessage() {
        val domain = ErrorApiDataModel.ApiServerErrorWithCode(503, "unavailable").toDomain()

        assertIs<ErrorApiModel.ApiServerErrorWithCode>(domain)
        assertEquals(503, domain.responseCode)
        assertEquals("unavailable", domain.responseMessage)
    }

    @Test
    fun mapsEveryOtherCase() {
        assertIs<ErrorApiModel.ApiNullBody>(ErrorApiDataModel.ApiNullBody().toDomain())
        assertIs<ErrorApiModel.ApiGenericServerError>(ErrorApiDataModel.ApiGenericServerError().toDomain())
        assertIs<ErrorApiModel.SocketTimeOutError>(ErrorApiDataModel.SocketTimeOutError().toDomain())
        assertIs<ErrorApiModel.NoInternetError>(ErrorApiDataModel.NoInternetError().toDomain())
    }
}
