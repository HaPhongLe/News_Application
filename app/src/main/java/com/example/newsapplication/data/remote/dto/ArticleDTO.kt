package com.example.newsapplication.data.remote.dto

import com.example.newsapplication.data.local.entity.ArticleEntity

data class ArticleDTO (
    val source: SourceDTO,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String
) {
    fun toArticleEntity(): ArticleEntity{
        return ArticleEntity(
            source = source.toSource(),
            author = author,
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            content = content
        )
    }
}