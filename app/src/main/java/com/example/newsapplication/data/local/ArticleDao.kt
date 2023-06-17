package com.example.newsapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapplication.data.local.entity.ArticleEntity
import com.example.newsapplication.data.local.entity.HeadlineEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM article")
    fun getArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM headline INNER JOIN article ON articleUrl = url")
    fun getHeadlines(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakingNews(headlines: List<HeadlineEntity>)
}