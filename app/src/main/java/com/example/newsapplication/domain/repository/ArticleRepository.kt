package com.example.newsapplication.domain.repository

import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.util.Resource
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun searchArticles(query: String): Flow<Resource<List<Article>>>

    fun getBreakingNews(): Flow<Resource<List<Article>>>

    fun getBookMarks(): Flow<List<Article>>

    suspend fun updateArticle(article: Article)
}