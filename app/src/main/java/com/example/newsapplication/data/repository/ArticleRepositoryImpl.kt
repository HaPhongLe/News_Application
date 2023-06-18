package com.example.newsapplication.data.repository

import com.example.newsapplication.data.local.ArticleDatabase
import com.example.newsapplication.data.remote.NewsApi
import com.example.newsapplication.data.util.networkBoundResource
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.repository.ArticleRepository
import com.example.newsapplication.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleRepositoryImpl(
    private val api: NewsApi,
    private val db: ArticleDatabase
): ArticleRepository {

    val dao = db.dao
    override fun getAllArticles(): Flow<Resource<List<Article>>> {
        return networkBoundResource(
            query = {dao.getArticles()} ,
            fetch = {api.getAllArticles()},
            saveFetchResult = {responseDTO -> dao.insertArticles(responseDTO.articles.map { articleDTO -> articleDTO.toArticleEntity() })},
            shouldFetch = {true},
            convertLocalToResult = {articleEntityList ->
                articleEntityList.map { articleEntity -> articleEntity.toArticle() }
            }
        )
    }

    override fun getBreakingNews(): Flow<Resource<List<Article>>> {
        return networkBoundResource(
            query = {dao.getHeadlines()},
            fetch = {api.getBreakingNews()},
            saveFetchResult = {responseDTO ->
                dao.insertArticles(responseDTO.articles.map { articleDTO -> articleDTO.toArticleEntity() })
                dao.insertBreakingNews(responseDTO.articles.map { articleDTO -> articleDTO.toHeadLineEntity() })
            },
            shouldFetch = {true},
            convertLocalToResult = {articleEntityList ->
                articleEntityList.map { articleEntity -> articleEntity.toArticle() }}
        )
    }

    override fun getBookMarks(): Flow<List<Article>> {
        return dao.getBookmarks().map { articles -> articles.map { articleEntity -> articleEntity.toArticle() } }
    }

    override suspend fun updateArticle(article: Article){
        dao.updateArticle(article.toArticleEntity())
    }
}