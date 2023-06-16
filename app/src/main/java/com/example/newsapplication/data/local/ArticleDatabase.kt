package com.example.newsapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapplication.data.local.entity.ArticleEntity
import com.example.newsapplication.data.local.entity.Converters


@Database(entities = [ArticleEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class ArticleDatabase: RoomDatabase() {

    abstract val dao:  ArticleDao
}