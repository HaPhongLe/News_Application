package com.example.newsapplication.data.repository

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.example.newsapplication.data.local.ArticleDatabase
import com.example.newsapplication.data.paging.ArticleRemoteMediator
import com.example.newsapplication.data.remote.NewsApi
import com.example.newsapplication.data.util.networkBoundResource
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.repository.ArticleRepository
import com.example.newsapplication.util.Constant
import com.example.newsapplication.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

    @OptIn(ExperimentalPagingApi::class)
    override fun getBreakingNews(): Flow<PagingData<Article>> =
    Pager(
            config = PagingConfig(pageSize = Constant.ITEMS_PER_PAGE),
            remoteMediator = ArticleRemoteMediator(
                api = api,
                db = db
            ),
            pagingSourceFactory = {dao.getBreakingNews()}
        ).flow
            .map { pagingData ->
            pagingData.map { articleEntity -> articleEntity.toArticle() }
        }

//        return networkBoundResource(
//            query = {dao.getBreakingNews()},
//            fetch = {
//                Log.d(TAG, "getBreakingNews: ")
//                api.getBreakingNews()},
//            saveFetchResult = {responseDTO ->
//
//                val serverArticles = responseDTO.articles
//                val bookmarks = dao.getBookmarks().first()
//                val localArticles = serverArticles.map { serverArticle ->
//                    if(bookmarks.any { bookmark -> bookmark.url == serverArticle.url }){
//                        serverArticle.toArticleEntity().copy(isBookmarked = true)
//                    }else{
//                        serverArticle.toArticleEntity()
//                    }
//                }
//
//                db.withTransaction {
//                    dao.deleteBreakingNews()
//                    dao.deleteBreakingNewsFromArticles()
//                    dao.insertArticles(localArticles)
//                    dao.insertBreakingNews(responseDTO.articles.map { articleDTO -> articleDTO.toHeadLineEntity() })
//                }
//
//            },
//            shouldFetch = {cachedArticles ->
//                if (forceRefresh || cachedArticles.isEmpty()){
//                    true
//                }else{
//
//                    val firstCachedArtcle = cachedArticles.first()
//                    val should =System.currentTimeMillis()- firstCachedArtcle.updateAt > TimeUnit.MINUTES.toMillis(5)
//                    Log.d("repository", "getBreakingNews: $should")
//                    should
//                } },
//            convertLocalToResult = {articleEntityList ->
//                articleEntityList.map { articleEntity -> articleEntity.toArticle() }}
//        )
//    }

    override fun getBookMarks(): Flow<List<Article>> {
        return dao.getBookmarks().map { articles -> articles.map { articleEntity -> articleEntity.toArticle() } }
    }

    override suspend fun updateArticle(article: Article){
        dao.updateArticle(article.toArticleEntity())
    }

    override suspend fun deleteNonBookmarkedArticlesOlderThan(time: Long) {
        dao.deleteNonBookmarkedArticlesOlderThan(time)
    }

    override suspend fun resetAllBookmarks() {
        dao.resetAllBookmarks()
    }


}