package com.example.newsapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapplication.data.local.entity.ArticleRemoteKey
import com.example.newsapplication.util.Constant

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticleRemoteKeys(remoteKeys: List<ArticleRemoteKey>)

    @Query("SELECT * FROM article_remote_key WHERE url = :url")
    suspend fun getArticleRemoteKey(url: String): ArticleRemoteKey

    @Query("DELETE FROM ${Constant.ARTICLE_REMOTE_KEY_TABLE}")
    suspend fun deleteAllArticleRemoteKeys()

    @Query("SELECT nextKey FROM article_remote_key ORDER BY nextKey DESC LIMIT 1")
    suspend fun getLastRemoteNextKey(): Int?
}