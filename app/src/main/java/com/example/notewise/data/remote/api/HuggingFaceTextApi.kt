package com.example.notewise.data.remote.api

import com.example.notewise.data.remote.model.ApiResponse
import com.example.notewise.data.remote.model.ApiRequest
import com.example.notewise.utils.Constants
import com.example.notewise.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HuggingFaceTextApi {
    @POST("/models/facebook/bart-large-mnli")
    suspend fun classifyText(
        @Body request: ApiRequest,
        @Header("Authorization") authToken: String = "Bearer $API_KEY"
    ): Response<ApiResponse>
}
