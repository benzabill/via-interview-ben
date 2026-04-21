package com.tseytlin.via.interview.data

import com.tseytlin.via.interview.data.repository.DefaultRequestRepository
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestResult
import com.tseytlin.via.interview.domain.service.RequestService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class DefaultRequestRepositoryTest {

    private val service: RequestService = mockk()
    private val repository = DefaultRequestRepository(service)

    @Test
    fun `currentRequest returns a stable sample`() {
        val first = repository.currentRequest()
        val second = repository.currentRequest()
        assertSame("repeated calls must return the same instance", first, second)
    }

    @Test
    fun `approve delegates to the service and returns its result`() = runTest {
        val request = Request(id = "42", title = "t", description = "d")
        coEvery { service.approve(request) } returns RequestResult.Success

        val result = repository.approve(request)

        assertEquals(RequestResult.Success, result)
        coVerify(exactly = 1) { service.approve(request) }
    }

    @Test
    fun `reject delegates to the service and returns its result`() = runTest {
        val request = Request(id = "42", title = "t", description = "d")
        val error = RequestResult.Error("nope")
        coEvery { service.reject(request) } returns error

        val result = repository.reject(request)

        assertEquals(error, result)
        coVerify(exactly = 1) { service.reject(request) }
    }
}
