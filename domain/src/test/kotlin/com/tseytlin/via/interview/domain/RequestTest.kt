package com.tseytlin.via.interview.domain

import com.tseytlin.via.interview.domain.model.Request
import kotlin.test.assertEquals
import org.junit.Test

class RequestTest {

    @Test
    fun `Request holds all fields`() {
        val request = Request(id = "1", title = "Heading 1", description = "Lorem ipsum")
        assertEquals("1", request.id)
        assertEquals("Heading 1", request.title)
        assertEquals("Lorem ipsum", request.description)
    }

    @Test
    fun `Request equality is structural`() {
        val a = Request(id = "1", title = "T", description = "D")
        val b = Request(id = "1", title = "T", description = "D")
        assertEquals(a, b)
    }
}
