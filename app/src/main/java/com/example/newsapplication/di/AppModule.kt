package com.example.newsapplication.di

import android.content.Context
import androidx.room.Room
import com.example.newsapplication.data.local.ArticleDatabase
import com.example.newsapplication.data.remote.NewsApi
import com.example.newsapplication.data.repository.ArticleRepositoryImpl
import com.example.newsapplication.data.util.GsonParser
import com.example.newsapplication.data.util.MoshiParser
import com.example.newsapplication.domain.repository.ArticleRepository
import com.example.newsapplication.domain.use_case.GetAllArticles
import com.example.newsapplication.domain.use_case.GetBreakingNews
import com.example.newsapplication.domain.use_case.UpdateBookmark
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideNewsApi():NewsApi{
//        val moshi = Moshi.Builder()
//            .add(KotlinJsonAdapterFactory())
//            .build()

        return Retrofit.Builder()
            .baseUrl(NewsApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideArticleDatabase(@ApplicationContext context: Context): ArticleDatabase{
        return Room.databaseBuilder(
            context = context,
            klass = ArticleDatabase::class.java,
            name = "article_db"
            )
            .fallbackToDestructiveMigration()
//            .addTypeConverter(Converters(MoshiParser(moshi)))
            .build()
    }

    @Provides
    @Singleton
    fun provideArticleRepository(
        api: NewsApi,
        db: ArticleDatabase
    ): ArticleRepository{
        return ArticleRepositoryImpl(api, db)
    }

    @Provides
    @Singleton
    fun provideGetAllArticles(repository: ArticleRepository): GetAllArticles{
        return GetAllArticles(repository)
    }

    @Provides
    @Singleton
    fun provideGetBreakingNews(repository: ArticleRepository): GetBreakingNews{
        return GetBreakingNews(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateBookmark(repository: ArticleRepository): UpdateBookmark{
        return UpdateBookmark(repository)
    }
}