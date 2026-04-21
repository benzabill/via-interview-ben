package com.tseytlin.via.interview.detail

import com.tseytlin.via.interview.detail.viewmodel.RequestDetailViewModel
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestOutcome
import com.tseytlin.via.interview.domain.model.RequestResult
import com.tseytlin.via.interview.domain.repository.RequestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

// Exercises the Koin graph end-to-end: swaps in a fake RequestRepository, then
// resolves the production RequestDetailViewModel through Koin and verifies the VM
// uses the injected fake. This catches the class of bug where the VM compiles
// against the interface but the module wiring points at the wrong binding.
//
// Uses `factory` instead of the production `viewModel` DSL because `by inject()`
// runs in a pure JVM context that has no ViewModelStoreOwner. Functionally both
// produce a new VM per resolution, so the scoping difference is irrelevant here.
@OptIn(ExperimentalCoroutinesApi::class)
class RequestDetailViewModelKoinTest : KoinTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeRequest = Request(id = "fake-id", title = "Fake", description = "From fake repo")

    private val fakeRepository = object : RequestRepository {
        var approveCalls = 0
        override fun currentRequest(): Request = fakeRequest
        override suspend fun approve(request: Request): RequestResult {
            approveCalls++
            return RequestResult.Success
        }
        override suspend fun reject(request: Request): RequestResult = RequestResult.Success
    }

    private val viewModel: RequestDetailViewModel by inject()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(
                module {
                    single<RequestRepository> { fakeRepository }
                    factory { RequestDetailViewModel(get()) }
                },
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun `VM resolved from Koin exposes request from the injected repository`() {
        assertEquals("fake-id", viewModel.request.id)
    }

    @Test
    fun `VM resolved from Koin drives approve through the injected repository`() = runTest(testDispatcher) {
        val outcomes = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeEvent.collect { outcomes.add(it) } }

        viewModel.approve()
        advanceUntilIdle()

        assertEquals(1, fakeRepository.approveCalls)
        assertTrue(outcomes.any { it is RequestOutcome.Approved })
        job.cancel()
    }
}
