package com.example.newsapplication.data.remote

import com.example.newsapplication.BuildConfig
import com.example.newsapplication.data.remote.dto.ResponseDTO
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface NewsApi {
    companion object{
        const val BASE_URL = "https://newsapi.org/v2/"
        const val API_KEY = BuildConfig.API_KEY
    }

    @Headers("X-Api-Key: $API_KEY")
    @GET("everything")
    suspend fun getAllArticles(
       @Query("q") query: String = "bitcoin"
    ): ResponseDTO

    @Headers("X-Api-Key: $API_KEY")
    @GET("top-headlines?country=us&pageSize=100")
    suspend fun getBreakingNews(): ResponseDTO
}