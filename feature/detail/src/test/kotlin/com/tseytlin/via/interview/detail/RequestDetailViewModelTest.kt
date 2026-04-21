package com.tseytlin.via.interview.detail

import com.tseytlin.via.interview.detail.viewmodel.RequestDetailViewModel
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestOutcome
import com.tseytlin.via.interview.domain.model.RequestResult
import com.tseytlin.via.interview.domain.repository.RequestRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequestDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val request = Request(id = "1", title = "Heading 1", description = "Lorem ipsum")
    private val mockRepository: RequestRepository = mockk()
    private lateinit var viewModel: RequestDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { mockRepository.currentRequest() } returns request
        viewModel = RequestDetailViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `approve success - isLoading flips on then off, successMessage set, Approved emitted`() = runTest(testDispatcher) {
        coEvery { mockRepository.approve(request) } coAnswers {
            delay(SERVICE_DELAY_MS)
            RequestResult.Success
        }

        val outcomes = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeEvent.collect { outcomes.add(it) } }

        viewModel.approve()
        runCurrent()
        assertTrue("isLoading should be true while service call is in flight", viewModel.isLoading.value)

        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Request approved", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)
        val approved = outcomes.filterIsInstance<RequestOutcome.Approved>().firstOrNull()
        assertEquals("Request approved", approved?.message)

        job.cancel()
    }

    @Test
    fun `approve failure - isLoading resets, raw service error surfaces in errorMessage and in ApprovalFailed outcome`() = runTest(testDispatcher) {
        val errorMsg = "Approval failed: server rejected the request"
        coEvery { mockRepository.approve(request) } returns RequestResult.Error(errorMsg)

        val outcomes = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeEvent.collect { outcomes.add(it) } }

        viewModel.approve()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.successMessage.value)
        assertEquals(errorMsg, viewModel.errorMessage.value)
        val approvalFailed = outcomes.filterIsInstance<RequestOutcome.ApprovalFailed>().firstOrNull()
        assertEquals(
            "ApprovalFailed must carry the raw service error so Home can show a descriptive snackbar",
            errorMsg,
            approvalFailed?.message,
        )

        job.cancel()
    }

    @Test
    fun `reject success - isLoading resets, successMessage set, Rejected emitted`() = runTest(testDispatcher) {
        coEvery { mockRepository.reject(request) } returns RequestResult.Success

        val outcomes = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeEvent.collect { outcomes.add(it) } }

        viewModel.reject()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Request rejected", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)
        val rejected = outcomes.filterIsInstance<RequestOutcome.Rejected>().firstOrNull()
        assertEquals("Request rejected", rejected?.message)

        job.cancel()
    }

    @Test
    fun `reject failure - isLoading resets, raw error in errorMessage, Rejected emitted with normalized text`() = runTest(testDispatcher) {
        val errorMsg = "Rejection failed: network error"
        coEvery { mockRepository.reject(request) } returns RequestResult.Error(errorMsg)

        val outcomes = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.outcomeEvent.collect { outcomes.add(it) } }

        viewModel.reject()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.successMessage.value)
        assertEquals(errorMsg, viewModel.errorMessage.value)
        val rejected = outcomes.filterIsInstance<RequestOutcome.Rejected>().firstOrNull()
        assertEquals("Request rejected", rejected?.message)

        job.cancel()
    }

    @Test
    fun `a new action clears stale success and error messages before the new result arrives`() = runTest(testDispatcher) {
        // First call fails and sets errorMessage. We then re-stub the next approve to suspend,
        // so we can observe the mid-flight state — which is precisely when the stale
        // errorMessage must already be cleared (before any new result arrives).
        val firstErrorMsg = "Approval failed: first call"
        coEvery { mockRepository.approve(request) } returns RequestResult.Error(firstErrorMsg)

        viewModel.approve()
        advanceUntilIdle()
        assertEquals(firstErrorMsg, viewModel.errorMessage.value)

        coEvery { mockRepository.approve(request) } coAnswers {
            delay(SERVICE_DELAY_MS)
            RequestResult.Success
        }
        viewModel.approve()
        runCurrent()
        assertTrue("sanity: second approve should be in flight", viewModel.isLoading.value)
        assertNull(
            "errorMessage from the prior failed call must be cleared at the start of the next action",
            viewModel.errorMessage.value,
        )
        advanceUntilIdle()
        assertEquals("Request approved", viewModel.successMessage.value)

        // Flip the direction: stale successMessage must also be cleared at the start of the next action.
        coEvery { mockRepository.reject(request) } coAnswers {
            delay(SERVICE_DELAY_MS)
            RequestResult.Success
        }
        viewModel.reject()
        runCurrent()
        assertNull(
            "successMessage from the prior approve must be cleared at the start of the next action",
            viewModel.successMessage.value,
        )
        advanceUntilIdle()
        assertEquals("Request rejected", viewModel.successMessage.value)
    }

    @Test
    fun `a second approve tapped while the first is in flight is a no-op`() = runTest(testDispatcher) {
        coEvery { mockRepository.approve(request) } coAnswers {
            delay(SERVICE_DELAY_MS)
            RequestResult.Success
        }

        viewModel.approve()
        // Synchronous re-entry: second call happens before the scheduler runs the first
        // coroutine. The guard must be based on _isLoading being set synchronously, not
        // inside the launched block, or this second call would be able to slip through.
        viewModel.approve()
        advanceUntilIdle()

        coVerify(exactly = 1) { mockRepository.approve(request) }
    }

    private companion object {
        const val SERVICE_DELAY_MS = 50L
    }
}
