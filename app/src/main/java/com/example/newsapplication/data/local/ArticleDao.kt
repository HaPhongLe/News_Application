package com.example.newsapplication.data.local

import androidx.room.*
import com.example.newsapplication.data.local.entity.ArticleEntity
import com.example.newsapplication.data.local.entity.HeadlineEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM article")
    fun getArticles(): Flow<List<ArticleEntity>>

    @Query("DELETE FROM article")
    fun deleteArticles()

    @Query("SELECT * FROM headline INNER JOIN article ON articleUrl = url")
    fun getHeadlines(): Flow<List<ArticleEntity>>

    @Query("DELETE FROM headline")
    fun deleteBreakingNews()

    @Query("DELETE FROM article  WHERE EXISTS (  SELECT * FROM headline INNER JOIN article ON articleUrl = url)")
    fun deleteBreakingNewsFromArticles()

    @Query("SELECT * FROM article WHERE isBookmarked = true")
    fun getBookmarks(): Flow<List<ArticleEntity>>

    @Query("UPDATE article SET isBookmarked = false")
    suspend fun resetAllBookmarks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakingNews(headlines: List<HeadlineEntity>)

    @Update
    suspend fun updateArticle(articleEntity: ArticleEntity)

    @Query("DELETE FROM article WHERE updateAt < :time AND isBookmarked = false")
    suspend fun deleteNonBookmarkedArticlesOlderThan(time: Long)

}