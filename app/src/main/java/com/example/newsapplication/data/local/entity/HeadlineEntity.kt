package com.example.newsapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.util.Constant


@Entity(tableName = Constant.ARTICLE_HEAD_LINE_TABLE)
data class HeadlineEntity(
    val articleUrl: String,
    @PrimaryKey val id: Int? = null
)
