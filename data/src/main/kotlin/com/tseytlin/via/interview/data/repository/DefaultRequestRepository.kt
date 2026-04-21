package com.tseytlin.via.interview.data.repository

import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestResult
import com.tseytlin.via.interview.domain.repository.RequestRepository
import com.tseytlin.via.interview.domain.service.RequestService

class DefaultRequestRepository(
    private val service: RequestService,
) : RequestRepository {

    override fun currentRequest(): Request = SAMPLE_REQUEST

    override suspend fun approve(request: Request): RequestResult = service.approve(request)

    override suspend fun reject(request: Request): RequestResult = service.reject(request)

    private companion object {
        val SAMPLE_REQUEST = Request(
            id = "1",
            title = "Heading 1",
            description = "Lorem ipsum dolor sit amet consectetur. Arcu tincidunt vitae cras amet. " +
                "Blandit id sed et est gravida. Eu sapien amet et volutpat ultrices sed. " +
                "Euismod semper mi non vitae egestas sollicitudin aliquam.",
        )
    }
}
