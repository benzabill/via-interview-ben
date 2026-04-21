package com.tseytlin.via.interview.domain.repository

import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestResult

interface RequestRepository {
    fun currentRequest(): Request
    suspend fun approve(request: Request): RequestResult
    suspend fun reject(request: Request): RequestResult
}
