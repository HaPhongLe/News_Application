package com.example.newsapplication.data.repository

import com.example.newsapplication.data.local.ArticleDatabase
import com.example.newsapplication.data.remote.NewsApi
import com.example.newsapplication.data.util.networkBoundResource
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.repository.ArticleRepository
import com.example.newsapplication.util.Resource
import kotlinx.coroutines.flow.Flow

class ArticleRepositoryImpl(
    private val api: NewsApi,
    private val db: ArticleDatabase
): ArticleRepository {
    override fun getAllArticles(): Flow<Resource<List<Article>>> {
        return networkBoundResource(
            query = {db.dao.getArticles()} ,
            fetch = {api.getAllArticles()},
            saveFetchResult = {responseDTO -> db.dao.insertArticles(responseDTO.articles.map { articleDTO -> articleDTO.toArticleEntity() })},
            shouldFetch = {true},
            convertLocalToResult = {articleEntityList ->
                articleEntityList.map { articleEntity -> articleEntity.toArticle() }
            }
        )
    }

    override fun getBreakingNews(): Flow<Resource<List<Article>>> {
        return networkBoundResource(
            query = {db.dao.getHeadlines()},
            fetch = {api.getBreakingNews()},
            saveFetchResult = {responseDTO ->
                db.dao.insertArticles(responseDTO.articles.map { articleDTO -> articleDTO.toArticleEntity() })
                db.dao.insertBreakingNews(responseDTO.articles.map { articleDTO -> articleDTO.toHeadLineEntity() })
            },
            shouldFetch = {true},
            convertLocalToResult = {articleEntityList ->
                articleEntityList.map { articleEntity -> articleEntity.toArticle() }}
        )
    }
}