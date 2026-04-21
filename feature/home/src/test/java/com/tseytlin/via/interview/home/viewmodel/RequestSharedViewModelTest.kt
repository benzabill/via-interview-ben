package com.tseytlin.via.interview.home.viewmodel

import com.tseytlin.via.interview.domain.model.RequestOutcome
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequestSharedViewModelTest {

    @Test
    fun `active collector receives emitted outcome`() = runTest(StandardTestDispatcher()) {
        val viewModel = RequestSharedViewModel()
        val received = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeFlow.collect { received.add(it) } }

        viewModel.emitOutcome(RequestOutcome.Approved("Request approved"))
        advanceUntilIdle()

        assertEquals(1, received.size)
        assertEquals(RequestOutcome.Approved("Request approved"), received[0])

        job.cancel()
    }

    @Test
    fun `outcome emitted before subscription is still delivered to later collector`() = runTest(StandardTestDispatcher()) {
        val viewModel = RequestSharedViewModel()

        viewModel.emitOutcome(RequestOutcome.Rejected("Request rejected"))
        advanceUntilIdle()

        val received = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeFlow.collect { received.add(it) } }
        advanceUntilIdle()

        assertEquals(
            "outcome emitted before a collector attached should survive in the buffer " +
                "so the snackbar fires after Home re-subscribes on back-nav",
            listOf<RequestOutcome>(RequestOutcome.Rejected("Request rejected")),
            received,
        )

        job.cancel()
    }

    @Test
    fun `multiple outcomes emitted before subscription are delivered in order`() = runTest(StandardTestDispatcher()) {
        val viewModel = RequestSharedViewModel()

        viewModel.emitOutcome(RequestOutcome.Approved("Request approved"))
        viewModel.emitOutcome(RequestOutcome.ApprovalFailed("Approval failed"))
        advanceUntilIdle()

        val received = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeFlow.collect { received.add(it) } }
        advanceUntilIdle()

        assertEquals(
            listOf(
                RequestOutcome.Approved("Request approved"),
                RequestOutcome.ApprovalFailed("Approval failed"),
            ),
            received,
        )

        job.cancel()
    }
}
