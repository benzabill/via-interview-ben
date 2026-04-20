package com.tseytlin.via.interview.domain

import com.tseytlin.via.interview.domain.model.RequestResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RequestResultTest {

    @Test
    fun `Success is distinct from Error`() {
        val results: List<RequestResult> = listOf(
            RequestResult.Success,
            RequestResult.Error("something went wrong"),
        )
        assertTrue(results[0] is RequestResult.Success)
        assertTrue(results[1] is RequestResult.Error)
    }

    @Test
    fun `Error carries its message`() {
        val message = "network timeout"
        val error = RequestResult.Error(message)
        assertEquals(message, error.message)
    }
}
