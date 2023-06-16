package com.example.newsapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.model.Source


@Entity(tableName = "article")
data class ArticleEntity(
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String,
    @PrimaryKey val id: Int? = null,
) {
    fun toArticle(): Article{
        return Article(
            source = source,
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
