package com.example.newsapplication.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapplication.data.local.ArticleDatabase
import com.example.newsapplication.data.local.entity.ArticleEntity
import com.example.newsapplication.data.local.entity.ArticleRemoteKey
import com.example.newsapplication.data.remote.NewsApi
import com.example.newsapplication.util.Constant
import kotlinx.coroutines.flow.first
import kotlin.math.log

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator (
       private val api: NewsApi,
       private val db: ArticleDatabase
        ) : RemoteMediator<Int, ArticleEntity>() {

    private val TAG ="RemoteMediator"

    private val articleDao = db.dao
    private val remoteKeyDao = db.remoteKeyDao


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {

        Log.d(TAG, "load: $state.")
        
        //calculate current page
        val requestPage: Int = when (loadType){
            LoadType.REFRESH -> {
                Log.d(TAG, "load: refresh")
                val remoteKey = getRemoteKeyClosetToCurrentPosition(state)
                remoteKey?.nextKey?.minus(1)?: 1
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                Log.d(TAG, "load: prepend remote key $remoteKey, prev page ${remoteKey?.prevKey}")
                val prevPage = remoteKey?.prevKey?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                prevPage
            }
            LoadType.APPEND -> {

                val remoteKey = getRemoteKeyForLastItem(state)
                val nextKey = remoteKey?.nextKey?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        //if current page returns
        //do fetch new data with that current page
        try {
            val response = api.getBreakingNews(page = requestPage, pageSize = Constant.ITEMS_PER_PAGE)
            val serverArticles = response.articles
            val isEndOfPagination = serverArticles.isEmpty()
            Log.d(TAG, "load: $requestPage $isEndOfPagination")

            val prevKey: Int? = if(requestPage == 1) null else  requestPage -1
            val nextKey: Int? = if (isEndOfPagination) null else requestPage + 1
            val bookmarks = articleDao.getBookmarks().first()
            Log.d(TAG, "load: bookmarks ${bookmarks.size}")
            val localArticles = serverArticles.map { serverArticle ->
                val isBookmarked = bookmarks.any { bookmark ->
                    bookmark.url == serverArticle.url
                }
                Log.d(TAG, "load: is bookmarked $isBookmarked")
                serverArticle.toArticleEntity().copy(isBookmarked = isBookmarked)
            }

            //update the database by inserting new entries of data and remotekeys

            db.withTransaction {
                if(loadType == LoadType.REFRESH){
                    articleDao.deleteBreakingNewsFromArticles()
                    articleDao.deleteBreakingNews()
                    remoteKeyDao.deleteAllArticleRemoteKeys()
                }

                articleDao.insertArticles(localArticles)
                articleDao.insertBreakingNews(serverArticles.map { serverArticle -> serverArticle.toHeadLineEntity() })
                remoteKeyDao.insertArticleRemoteKeys(serverArticles.map { serverArticle -> ArticleRemoteKey(
                    url = serverArticle.url,
                    prevKey = prevKey,
                    nextKey = nextKey
                    ) })
            }
            return MediatorResult.Success(isEndOfPagination)
        }catch (e: java.lang.Exception){
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosetToCurrentPosition(
        state: PagingState<Int, ArticleEntity>
    ): ArticleRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { article ->
                remoteKeyDao.getArticleRemoteKey(article.url)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, ArticleEntity>
    ): ArticleRemoteKey? {
        return state.pages.firstOrNull(){
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let {article ->
            remoteKeyDao.getArticleRemoteKey(article.url)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, ArticleEntity>
    ): ArticleRemoteKey? {
        Log.d(TAG, "getRemoteKeyForLastItem: $state")
        return state.pages.lastOrNull(){it.data.isNotEmpty()}?.data?.lastOrNull()?.let {article ->
            remoteKeyDao.getArticleRemoteKey(article.url)
        }
    }
}