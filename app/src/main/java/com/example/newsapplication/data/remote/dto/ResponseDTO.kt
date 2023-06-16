package com.example.newsapplication.data.remote.dto

data class ResponseDTO(
    val articles: List<ArticleDTO>,
    val status: String,
    val totalResults: Int
)