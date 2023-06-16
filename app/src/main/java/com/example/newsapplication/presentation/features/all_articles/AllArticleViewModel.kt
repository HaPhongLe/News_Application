package com.example.newsapplication.presentation.features.all_articles

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.domain.use_case.GetAllArticles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class AllArticleViewModel @Inject constructor(
    private val getAllArticle: GetAllArticles
): ViewModel() {

    private val TAG = "AllArticleViewModel"

    fun getArticles(){
        viewModelScope.launch {
            getAllArticle().collect{ result->
                Log.d(TAG, "getArticles: ${result.data} ${result.error?.message}")
            }
        }
    }
}