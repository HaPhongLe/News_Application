package com.example.newsapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapplication.domain.model.Article


@Entity(tableName = "headline")
data class HeadlineEntity(
    val articleUrl: String,
    @PrimaryKey val id: Int? = null
)
