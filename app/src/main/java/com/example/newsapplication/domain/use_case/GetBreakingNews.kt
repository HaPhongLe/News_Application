package com.example.newsapplication.domain.use_case

import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.repository.ArticleRepository
import com.example.newsapplication.util.Resource
import kotlinx.coroutines.flow.Flow

class GetBreakingNews(
    private val respository: ArticleRepository
) {
    operator fun invoke() : Flow<Resource<List<Article>>>{
       return respository.getBreakingNews()
    }
}