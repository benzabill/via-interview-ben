package com.tseytlin.via.interview.data.service

import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestResult
import com.tseytlin.via.interview.domain.service.RequestService
import kotlinx.coroutines.delay

class MockRequestService(
    private val random: () -> Double = { Math.random() },
) : RequestService {

    override suspend fun approve(request: Request): RequestResult {
        delay(2_000)
        return if (random() < 0.5) {
            RequestResult.Success
        } else {
            RequestResult.Error("Approval failed: server rejected the request")
        }
    }

    override suspend fun reject(request: Request): RequestResult {
        return RequestResult.Error("Request rejected")
    }
}
