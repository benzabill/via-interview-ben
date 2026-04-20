package com.tseytlin.via.interview.data

import com.tseytlin.via.interview.data.service.MockRequestService
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockRequestServiceTest {

    private val request = Request(id = "1", title = "Heading 1", description = "Lorem ipsum")

    @Test
    fun `reject returns Error immediately`() = runTest {
        val service = MockRequestService()
        val result = service.reject(request)
        assertTrue(result is RequestResult.Error)
        assertEquals("Request rejected", (result as RequestResult.Error).message)
    }

    @Test
    fun `approve returns Success when random is below 0_5`() = runTest {
        val service = MockRequestService(random = { 0.1 })
        val result = service.approve(request)
        assertTrue(result is RequestResult.Success)
    }

    @Test
    fun `approve returns Error when random is 0_5 or above`() = runTest {
        val service = MockRequestService(random = { 0.9 })
        val result = service.approve(request)
        assertTrue(result is RequestResult.Error)
        assertTrue((result as RequestResult.Error).message.isNotBlank())
    }
}
