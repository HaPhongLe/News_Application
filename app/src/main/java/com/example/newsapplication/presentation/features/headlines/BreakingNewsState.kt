package com.example.newsapplication.presentation.features.headlines

import com.example.newsapplication.domain.model.Article

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}


data class BreakingNewsState(
    val status: Status,
    val data: List<Article>?,
    val error: String? = null
) {
}