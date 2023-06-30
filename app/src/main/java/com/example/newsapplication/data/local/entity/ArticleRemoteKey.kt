package com.example.newsapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapplication.util.Constant


@Entity(tableName = Constant.ARTICLE_REMOTE_KEY_TABLE)
data class ArticleRemoteKey(
    @PrimaryKey val url: String,
    val prevKey: Int?,
    val nextKey: Int?
)
