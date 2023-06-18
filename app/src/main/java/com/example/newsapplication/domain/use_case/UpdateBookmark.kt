package com.example.newsapplication.domain.use_case

import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.repository.ArticleRepository

class UpdateBookmark(private val repository: ArticleRepository) {
    suspend operator fun invoke(article: Article){
        repository.updateArticle(article)
    }
}