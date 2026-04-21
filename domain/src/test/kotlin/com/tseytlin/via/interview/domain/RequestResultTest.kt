package com.tseytlin.via.interview.domain

import com.tseytlin.via.interview.domain.model.RequestResult
import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.junit.Test

class RequestResultTest {

    @Test
    fun `Success is distinct from Error`() {
        val results: List<RequestResult> = listOf(
            RequestResult.Success,
            RequestResult.Error("something went wrong"),
        )
        assertIs<RequestResult.Success>(results[0])
        assertIs<RequestResult.Error>(results[1])
    }

    @Test
    fun `Error carries its message`() {
        val message = "network timeout"
        val error = RequestResult.Error(message)
        assertEquals(message, error.message)
    }
}
