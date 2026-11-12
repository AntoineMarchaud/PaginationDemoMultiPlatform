package com.amarchaud.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.amarchaud.domain.models.CoordinatesModel
import com.amarchaud.domain.models.LocationModel
import com.amarchaud.domain.models.NameModel
import com.amarchaud.domain.models.UserModel
import com.amarchaud.domain.usecase.GetOneUserUseCase
import com.amarchaud.ui.fakes.FakePaginationDemoRepository
import com.amarchaud.ui.screen.detail.mappers.toDetailUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val user = UserModel(localId = 42, email = "jo@doe.fr", name = NameModel("Mr", "Jo", "Doe"))

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        repository: FakePaginationDemoRepository = FakePaginationDemoRepository(users = mapOf(42L to user)),
        id: Long? = 42,
    ) = UserDetailViewModel(
        stateHandle = SavedStateHandle(if (id != null) mapOf("id" to id) else emptyMap()),
        getUserFromCacheUseCase = GetOneUserUseCase(repository),
    )

    @Test
    fun loadsUserIntoState() = runTest(dispatcher) {
        val viewModel = viewModel()

        advanceUntilIdle()

        assertEquals(UserDetailState(user = user.toDetailUiModel()), viewModel.state.value)
    }

    @Test
    fun unknownUserKeepsDefaultState() = runTest(dispatcher) {
        val viewModel = viewModel(repository = FakePaginationDemoRepository())

        advanceUntilIdle()

        assertEquals(UserDetailState(), viewModel.state.value)
    }

    @Test
    fun backClickEmitsNavigateBack() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.onAction(UserDetailAction.BackClicked)
            assertEquals(UserDetailEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun missingIdThrows() {
        assertFailsWith<NullPointerException> { viewModel(id = null) }
    }

    @Test
    fun emailClickOpensMailApp() = runTest(dispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onAction(UserDetailAction.EmailClicked)
            assertEquals(UserDetailEvent.OpenUri("mailto:jo@doe.fr"), awaitItem())
        }
    }

    @Test
    fun phoneClickOpensDialer() = runTest(dispatcher) {
        val withPhone = user.copy(phone = "(568)-263-5288")
        val viewModel = viewModel(repository = FakePaginationDemoRepository(users = mapOf(42L to withPhone)))
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onAction(UserDetailAction.PhoneClicked)
            assertEquals(UserDetailEvent.OpenUri("tel:5682635288"), awaitItem())
        }
    }

    @Test
    fun addressClickOpensMaps() = runTest(dispatcher) {
        val located = user.copy(
            location = LocationModel(coordinates = CoordinatesModel(latitude = "48.85", longitude = "2.35")),
        )
        val viewModel = viewModel(repository = FakePaginationDemoRepository(users = mapOf(42L to located)))
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onAction(UserDetailAction.AddressClicked)
            assertEquals(
                UserDetailEvent.OpenUri("https://www.google.com/maps/search/?api=1&query=48.85,2.35"),
                awaitItem(),
            )
        }
    }

    @Test
    fun contactActionWithoutDataEmitsNothing() = runTest(dispatcher) {
        // the test user has no phone number
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onAction(UserDetailAction.PhoneClicked)
            expectNoEvents()
        }
    }
}
