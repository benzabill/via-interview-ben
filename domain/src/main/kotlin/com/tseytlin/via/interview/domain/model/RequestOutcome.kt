package com.tseytlin.via.interview.domain.model

sealed class RequestOutcome {
    abstract val message: String

    data class Approved(override val message: String) : RequestOutcome()
    data class Rejected(override val message: String) : RequestOutcome()
    data class ApprovalFailed(override val message: String) : RequestOutcome()
}
