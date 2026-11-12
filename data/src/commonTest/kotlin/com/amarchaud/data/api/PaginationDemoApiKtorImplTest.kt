package com.amarchaud.data.api

import com.amarchaud.data.models.ErrorApiDataModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PaginationDemoApiKtorImplTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    private fun api(handler: MockRequestHandler): PaginationDemoApi =
        PaginationDemoApiKtorImpl(HttpClient(MockEngine(handler)) { configurePaginationDemo() })

    @Test
    fun successParsesUsersAndSendsQueryParams() = runTest {
        var sentRequest: HttpRequestData? = null
        val api = api { request ->
            sentRequest = request
            respond("""{"results":[{"email":"a@b.c"}],"info":{"page":2}}""", HttpStatusCode.OK, jsonHeaders)
        }

        val result = api.getRandomUsers(page = 2)

        val request = assertNotNull(sentRequest)
        assertEquals("randomuser.me", request.url.host)
        assertEquals("/api/1.3/", request.url.encodedPath)
        assertEquals("PaginationDemo", request.url.parameters["seed"])
        assertEquals("20", request.url.parameters["results"])
        assertEquals("2", request.url.parameters["page"])
        assertEquals("a@b.c", result.getOrThrow().users.single().email)
        assertEquals(2, result.getOrThrow().info?.page)
    }

    @Test
    fun serverErrorWithJsonBodyIsFailure() = runTest {
        val api = api { respond("""{"results":[]}""", HttpStatusCode.InternalServerError, jsonHeaders) }

        assertTrue(api.getRandomUsers(page = 1).isFailure)
    }

    @Test
    fun invalidJsonIsFailure() = runTest {
        val api = api { respond("not json", HttpStatusCode.OK, jsonHeaders) }

        assertTrue(api.getRandomUsers(page = 1).isFailure)
    }

    @Test
    fun serverErrorIsMappedToApiServerErrorWithCode() = runTest {
        val api = api { respond("""{"results":[]}""", HttpStatusCode.ServiceUnavailable, jsonHeaders) }

        val error = api.getRandomUsers(page = 1).exceptionOrNull()

        assertIs<ErrorApiDataModel.ApiServerErrorWithCode>(error)
        assertEquals(503, error.responseCode)
        assertEquals("Service Unavailable", error.responseMessage)
    }

    @Test
    fun networkFailureIsMappedToNoInternetError() = runTest {
        val api = api { throw IOException("unable to resolve host") }

        assertIs<ErrorApiDataModel.NoInternetError>(api.getRandomUsers(page = 1).exceptionOrNull())
    }

    @Test
    fun timeoutIsMappedToSocketTimeOutError() = runTest {
        val api = api { throw ConnectTimeoutException("connect timeout") }

        assertIs<ErrorApiDataModel.SocketTimeOutError>(api.getRandomUsers(page = 1).exceptionOrNull())
    }
}
