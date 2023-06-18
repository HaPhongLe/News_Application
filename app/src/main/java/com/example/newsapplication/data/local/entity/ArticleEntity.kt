package com.example.newsapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapplication.domain.model.Article


@Entity(tableName = "article")
data class ArticleEntity(
    val source: String,
    val title: String,
    val description: String?,
    @PrimaryKey val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val isBookmarked: Boolean = false
) {
    fun toArticle(): Article{
        return Article(
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
