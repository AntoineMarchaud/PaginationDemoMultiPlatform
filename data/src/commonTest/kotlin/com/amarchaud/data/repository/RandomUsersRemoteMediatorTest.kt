package com.amarchaud.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import com.amarchaud.data.fakes.FakePaginationDemoApi
import com.amarchaud.data.fakes.FakePaginationDemoDao
import com.amarchaud.data.models.ErrorApiDataModel
import com.amarchaud.data.models.InfoDataModel
import com.amarchaud.data.models.ResultsDataModel
import com.amarchaud.data.models.UserDataModel
import com.amarchaud.data.models.UserEntityModel
import com.amarchaud.data.repository.remotemediator.RandomUsersRemoteMediator
import com.amarchaud.domain.models.ErrorApiModel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalPagingApi::class)
class RandomUsersRemoteMediatorTest {

    private val dao = FakePaginationDemoDao()
    private val api = FakePaginationDemoApi()
    private val mediator = RandomUsersRemoteMediator(paginationDemoDao = dao, paginationDemoApi = api)
    private val state = PagingState<Int, UserEntityModel>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0,
    )

    private fun apiUsers(count: Int, page: Int) = Result.success(
        ResultsDataModel(
            users = List(count) { UserDataModel(email = "user$it@test.fr") },
            info = InfoDataModel(page = page),
        )
    )

    @Test
    fun refreshSuccessClearsTablesAndInsertsPageAndUsers() = runTest {
        dao.users += UserEntityModel(_id = 99, email = "old@test.fr")
        dao.pages += 5
        api.result = apiUsers(count = 2, page = 1)

        val result = mediator.load(LoadType.REFRESH, state)

        assertIs<MediatorResult.Success>(result)
        assertFalse(result.endOfPaginationReached)
        assertEquals(listOf(1), api.requestedPages)
        assertEquals(listOf("user0@test.fr", "user1@test.fr"), dao.users.map { it.email })
        assertEquals(listOf(1), dao.pages)
    }

    @Test
    fun appendRequestsLastPagePlusOne() = runTest {
        dao.pages += 3
        api.result = apiUsers(count = 1, page = 4)

        val result = mediator.load(LoadType.APPEND, state)

        assertIs<MediatorResult.Success>(result)
        assertFalse(result.endOfPaginationReached)
        assertEquals(listOf(4), api.requestedPages)
        assertEquals(listOf(3, 4), dao.pages)
    }

    @Test
    fun appendWithoutStoredPageEndsPagination() = runTest {
        val result = mediator.load(LoadType.APPEND, state)

        assertIs<MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        assertTrue(api.requestedPages.isEmpty())
    }

    @Test
    fun prependEndsPagination() = runTest {
        val result = mediator.load(LoadType.PREPEND, state)

        assertIs<MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        assertTrue(api.requestedPages.isEmpty())
    }

    @Test
    fun emptyResponseEndsPagination() = runTest {
        api.result = apiUsers(count = 0, page = 1)

        val result = mediator.load(LoadType.REFRESH, state)

        assertIs<MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
    }

    @Test
    fun apiDataErrorIsMappedToDomainError() = runTest {
        api.result = Result.failure(ErrorApiDataModel.NoInternetError())

        val result = mediator.load(LoadType.REFRESH, state)

        assertIs<MediatorResult.Error>(result)
        assertIs<ErrorApiModel.NoInternetError>(result.throwable)
    }

    @Test
    fun unknownErrorBecomesGenericError() = runTest {
        api.result = Result.failure(IllegalStateException("boom"))

        val result = mediator.load(LoadType.REFRESH, state)

        assertIs<MediatorResult.Error>(result)
        assertIs<ErrorApiModel.GenericError>(result.throwable)
    }

    @Test
    fun refreshFailureKeepsCachedUsers() = runTest {
        dao.users += List(3) { UserEntityModel(_id = it.toLong(), email = "cached$it@test.fr") }
        dao.pages += 1
        api.result = Result.failure(IllegalStateException("offline"))

        mediator.load(LoadType.REFRESH, state)

        assertEquals(3, dao.users.size)
        assertEquals(listOf(1), dao.pages)
    }
}
