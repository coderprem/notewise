package com.example.notewise.data.remote.api

import kotlinx.serialization.Serializable

@Serializable
data class Parameters(
    val candidate_labels: List<String>,
    val multi_label: Boolean = false,
)