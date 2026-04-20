package com.tseytlin.via.interview.domain.service

import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestResult

interface RequestService {
    suspend fun approve(request: Request): RequestResult
    suspend fun reject(request: Request): RequestResult
}
