package com.tseytlin.via.interview.home.viewmodel

import com.tseytlin.via.interview.domain.model.RequestOutcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequestSharedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `active collector receives emitted outcome`() = runTest(testDispatcher) {
        val viewModel = RequestSharedViewModel()
        val received = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeFlow.collect { received.add(it) } }

        viewModel.emitOutcome(RequestOutcome.Approved("Request approved"))
        advanceUntilIdle()

        assertEquals(listOf<RequestOutcome>(RequestOutcome.Approved("Request approved")), received)

        job.cancel()
    }

    @Test
    fun `outcome emitted before subscription is still delivered to later collector`() = runTest(testDispatcher) {
        val viewModel = RequestSharedViewModel()

        viewModel.emitOutcome(RequestOutcome.Rejected("Request rejected"))
        advanceUntilIdle()

        val received = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeFlow.collect { received.add(it) } }
        advanceUntilIdle()

        assertEquals(
            listOf<RequestOutcome>(RequestOutcome.Rejected("Request rejected")),
            received,
            "outcome emitted before a collector attached should survive in the replay cache " +
                "so the snackbar fires after Home re-subscribes on back-nav",
        )

        job.cancel()
    }

    @Test
    fun `consumeOutcome clears the replay cache so a later collector does not re-receive the event`() = runTest(testDispatcher) {
        val viewModel = RequestSharedViewModel()

        viewModel.emitOutcome(RequestOutcome.Approved("Request approved"))
        advanceUntilIdle()
        viewModel.consumeOutcome()

        val received = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeFlow.collect { received.add(it) } }
        advanceUntilIdle()

        assertEquals(emptyList<RequestOutcome>(), received)

        job.cancel()
    }
}
