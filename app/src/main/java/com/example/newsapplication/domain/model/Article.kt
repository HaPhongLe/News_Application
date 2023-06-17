package com.example.newsapplication.domain.model


data class Article (
    val source: String,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    ) {
}