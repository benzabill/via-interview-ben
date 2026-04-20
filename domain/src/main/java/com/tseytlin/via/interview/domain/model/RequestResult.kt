package com.tseytlin.via.interview.domain.model

sealed class RequestResult {
    data object Success : RequestResult()
    data class Error(val message: String) : RequestResult()
}
