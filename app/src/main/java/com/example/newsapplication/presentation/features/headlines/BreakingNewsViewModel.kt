package com.example.newsapplication.presentation.features.headlines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.use_case.DeleteNonBookmarkedArticlesOlderThan
import com.example.newsapplication.domain.use_case.GetBreakingNews
import com.example.newsapplication.domain.use_case.UpdateBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

enum class RefreshType{
    Manual,
    Auto
}

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val getBreakingNews: GetBreakingNews,
    private val updateBookmark: UpdateBookmark,
    private val deleteNonBookmarkedArticlesOlderThan: DeleteNonBookmarkedArticlesOlderThan
): ViewModel() {
    
    private val TAG = "GetBreakingNews"
    private val articleLifeSpanInDays: Long = 7

    private val refreshTriggerChannel = Channel<RefreshType>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    var scrollTotop: Boolean = false
//
    private val _state = MutableStateFlow<BreakingNewsState>(BreakingNewsState(status = Status.LOADING, data = emptyList()))
    val state: StateFlow<BreakingNewsState> = _state

    val lastOpenTime = System.currentTimeMillis()
    var firstFetch = true

    var breakingNews : Flow<PagingData<Article>> = refreshTrigger.flatMapLatest {
        getBreakingNews()
    }.cachedIn(viewModelScope)


    fun manualRefresh(){
        Log.d(TAG, "manualRefresh: ")
        viewModelScope.launch {
            Log.d(TAG, "manualRefresh: fsdfd ")
            refreshTriggerChannel.send(RefreshType.Manual)
        }
        scrollTotop = true
    }

    fun autoRefresh(){
        Log.d(TAG, "manualRefresh: ")
        val shouldFetch = System.currentTimeMillis() - lastOpenTime > TimeUnit.DAYS.toMillis(1)
        if (!shouldFetch && !firstFetch) return
        viewModelScope.launch {
            Log.d(TAG, "manualRefresh: fsdfd ")
            refreshTriggerChannel.send(RefreshType.Auto)
            firstFetch = false
        }
//        scrollTotop = true
    }

    fun onBookmarkClick(article: Article){
        val updatedArticle = article.copy(isBookmarked = !article.isBookmarked)
        viewModelScope.launch {
            updateBookmark(updatedArticle)
        }
    }


}

