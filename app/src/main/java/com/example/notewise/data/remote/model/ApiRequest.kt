package com.example.notewise.data.remote.model

import com.example.notewise.data.remote.api.Parameters
import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest(
    val inputs: String,
    val parameters: Parameters
)