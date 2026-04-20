package com.tseytlin.via.interview.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: String,
    val title: String,
    val description: String,
)
