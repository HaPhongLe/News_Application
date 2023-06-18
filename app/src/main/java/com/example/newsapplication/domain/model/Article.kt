package com.example.newsapplication.domain.model

import com.example.newsapplication.data.local.entity.ArticleEntity


data class Article (
    val source: String,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val isBookmarked: Boolean = false
    ) {

    fun toArticleEntity(): ArticleEntity{
        return ArticleEntity(
            source = source,
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            isBookmarked = isBookmarked
        )
    }
}