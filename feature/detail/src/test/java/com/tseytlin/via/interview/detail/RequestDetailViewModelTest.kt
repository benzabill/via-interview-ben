package com.tseytlin.via.interview.detail

import com.tseytlin.via.interview.detail.viewmodel.RequestDetailViewModel
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestOutcome
import com.tseytlin.via.interview.domain.model.RequestResult
import com.tseytlin.via.interview.domain.service.RequestService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
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
    private val mockService: RequestService = mockk()
    private lateinit var viewModel: RequestDetailViewModel

    private val request = Request(id = "1", title = "Heading 1", description = "Lorem ipsum")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RequestDetailViewModel(mockService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `approve success - isLoading resets, successMessage set, Approved emitted`() = runTest(testDispatcher) {
        coEvery { mockService.approve(request) } returns RequestResult.Success

        val outcomes = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.navigationEvent.collect { outcomes.add(it) } }

        viewModel.approve(request)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Request approved", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)
        val approved = outcomes.filterIsInstance<RequestOutcome.Approved>().firstOrNull()
        assertEquals("Request approved", approved?.message)

        job.cancel()
    }

    @Test
    fun `approve failure - isLoading resets, errorMessage set, ApprovalFailed emitted`() = runTest(testDispatcher) {
        val errorMsg = "Approval failed: server rejected the request"
        coEvery { mockService.approve(request) } returns RequestResult.Error(errorMsg)

        val outcomes = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.navigationEvent.collect { outcomes.add(it) } }

        viewModel.approve(request)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.successMessage.value)
        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertTrue(outcomes.any { it is RequestOutcome.ApprovalFailed })

        job.cancel()
    }

    @Test
    fun `reject - isLoading resets, errorMessage set, Rejected emitted`() = runTest(testDispatcher) {
        val errorMsg = "Request rejected"
        coEvery { mockService.reject(request) } returns RequestResult.Error(errorMsg)

        val outcomes = mutableListOf<RequestOutcome>()
        val job = launch { viewModel.navigationEvent.collect { outcomes.add(it) } }

        viewModel.reject(request)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertTrue(outcomes.any { it is RequestOutcome.Rejected })

        job.cancel()
    }
}
