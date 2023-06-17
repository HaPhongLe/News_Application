package com.example.newsapplication.data.remote.dto

import com.example.newsapplication.data.local.entity.ArticleEntity
import com.example.newsapplication.data.local.entity.HeadlineEntity

data class ArticleDTO (
    val source: SourceDTO,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
) {
    fun toArticleEntity(): ArticleEntity{
        return ArticleEntity(
            source = source.name,
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
       )
    }
    fun toHeadLineEntity(): HeadlineEntity{
        return HeadlineEntity(
            articleUrl = url
        )
    }
}