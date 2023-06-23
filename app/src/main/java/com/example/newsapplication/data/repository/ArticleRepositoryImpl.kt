package com.example.newsapplication.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.newsapplication.data.local.ArticleDatabase
import com.example.newsapplication.data.remote.NewsApi
import com.example.newsapplication.data.util.networkBoundResource
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.repository.ArticleRepository
import com.example.newsapplication.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class ArticleRepositoryImpl(
    private val api: NewsApi,
    private val db: ArticleDatabase
): ArticleRepository {
    private val TAG = "Repository Impl"

    val dao = db.dao
    override fun searchArticles(query: String): Flow<Resource<List<Article>>> {
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

    override fun getBreakingNews(forceRefresh: Boolean): Flow<Resource<List<Article>>> {
        return networkBoundResource(
            query = {dao.getHeadlines()},
            fetch = {
                Log.d(TAG, "getBreakingNews: ")
                api.getBreakingNews()},
            saveFetchResult = {responseDTO ->
                db.withTransaction {
                    dao.deleteBreakingNews()
                    dao.deleteBreakingNewsFromArticles()
                    dao.insertArticles(responseDTO.articles.map { articleDTO -> articleDTO.toArticleEntity() })
                    dao.insertBreakingNews(responseDTO.articles.map { articleDTO -> articleDTO.toHeadLineEntity() })
                }

            },
            shouldFetch = {cachedArticles ->
                if (forceRefresh || cachedArticles.isEmpty()){
                    true
                }else{

                    val firstCachedArtcle = cachedArticles.first()
                    val should =System.currentTimeMillis()- firstCachedArtcle.updateAt > TimeUnit.MINUTES.toMillis(5)
                    Log.d("repository", "getBreakingNews: $should")
                    should
                } },
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

    override suspend fun deleteNonBookmarkedArticlesOlderThan(time: Long) {
        dao.deleteNonBookmarkedArticlesOlderThan(time)
    }
}