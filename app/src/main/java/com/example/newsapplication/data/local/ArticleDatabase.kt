package com.example.newsapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapplication.data.local.entity.ArticleEntity
import com.example.newsapplication.data.local.entity.ArticleRemoteKey
import com.example.newsapplication.data.local.entity.HeadlineEntity


@Database(entities = [ArticleEntity::class, HeadlineEntity::class, ArticleRemoteKey::class], version = 3)
//@TypeConverters(Converters::class)
abstract class ArticleDatabase: RoomDatabase() {

    abstract val dao:  ArticleDao

    abstract val remoteKeyDao: RemoteKeyDao
}