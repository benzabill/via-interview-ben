package com.tseytlin.via.interview.domain.model

sealed class RequestOutcome {
    data object Approved : RequestOutcome()
    data class Rejected(val message: String) : RequestOutcome()
    data class ApprovalFailed(val message: String) : RequestOutcome()
}
