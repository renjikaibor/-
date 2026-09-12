package com.aicodeassistant.data.remote

import com.squareup.moshi.Json
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Url
import kotlinx.coroutines.flow.Flow

interface ApiService {

    @Streaming
    @POST("chat/completions")
    fun chatCompletion(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ChatCompletionRequest
    ): Flow<ResponseBody>

    @POST("chat/completions")
    fun chatCompletionNonStream(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ChatCompletionRequest
    ): retrofit2.Call<Response<ChatCompletionResponse>>

    @GET("models")
    fun listModels(
        @Header("Authorization") authHeader: String
    ): retrofit2.Call<Response<ModelsResponse>>

    // For custom base URLs
    @Streaming
    @POST
    fun chatCompletionCustom(
        @Url url: String,
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ChatCompletionRequest
    ): Flow<ResponseBody>

    @POST
    fun chatCompletionNonStreamCustom(
        @Url url: String,
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ChatCompletionRequest
    ): retrofit2.Call<Response<ChatCompletionResponse>>

    @GET
    fun listModelsCustom(
        @Url url: String,
        @Header("Authorization") authHeader: String
    ): retrofit2.Call<Response<ModelsResponse>>

    // Serper.dev Search API
    @POST("search")
    fun serperSearch(
        @Header("X-API-KEY") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: SearchRequest
    ): retrofit2.Call<Response<SearchResponse>>
}
