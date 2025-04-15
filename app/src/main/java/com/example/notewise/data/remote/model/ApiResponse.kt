package com.example.notewise.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val sequence: String,
    val labels: List<String>,
    val scores: List<Float>
)