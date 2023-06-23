package com.example.newsapplication.domain.use_case

import com.example.newsapplication.domain.repository.ArticleRepository

class ResetAllBookmarks(
    private val repository: ArticleRepository
) {

    suspend operator fun invoke(){
        repository.resetAllBookmarks()
    }
}