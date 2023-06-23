package com.example.newsapplication.domain.use_case

import com.example.newsapplication.domain.repository.ArticleRepository

class DeleteNonBookmarkedArticlesOlderThan (
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(time: Long) {
        repository.deleteNonBookmarkedArticlesOlderThan(time)
    }
}