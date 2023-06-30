package com.example.newsapplication.presentation.features.headlines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.repository.ArticleRepository
import com.example.newsapplication.domain.use_case.DeleteNonBookmarkedArticlesOlderThan
import com.example.newsapplication.domain.use_case.GetBreakingNews
import com.example.newsapplication.domain.use_case.UpdateBookmark
import com.example.newsapplication.util.Resource
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


//    val breakingNews = refreshTrigger.flatMapLatest { value: RefreshType ->
//        getBreakingNews()
//    }.cachedIn(viewModelScope)

    val breakingNews = getBreakingNews().cachedIn(viewModelScope)

//    init {
//        viewModelScope.launch {
//            deleteNonBookmarkedArticlesOlderThan(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(articleLifeSpanInDays))
//            refreshTrigger.collectLatest{ refreshType ->
//                Log.d(TAG, "trigger: ${refreshType.name} ${refreshType == RefreshType.Manual}")
//
//                getBreakingNews (refreshType == RefreshType.Manual).collect{result ->
//                    Log.d(TAG, "getHeadlines: ")
//                    when(result){
//                        is Resource.Loading -> {
//                            _state.value = BreakingNewsState(Status.LOADING, data = result.data?: emptyList())
//                        }
//                        is Resource.Success -> {
//                            _state.value = BreakingNewsState(Status.SUCCESS, data = result.data?: emptyList())
//                        }
//                        is Resource.Error -> {
//                            _state.value = BreakingNewsState(Status.ERROR, data = result.data?: emptyList(), error = result.error?.message?: "Unknown Error")
//                        }
//                        else -> {return@collect}
//                    }
//                }
//
//            }
//        }
//    }


    fun autoRefresh(){
        viewModelScope.launch {
            refreshTriggerChannel.send(RefreshType.Auto)
        }
    }

    fun manualRefresh(){
        Log.d(TAG, "manualRefresh: ")
        viewModelScope.launch {
            Log.d(TAG, "manualRefresh: fsdfd ")
            refreshTriggerChannel.send(RefreshType.Manual)
        }
        scrollTotop = true
    }


    fun onBookmarkClick(article: Article){
        val updatedArticle = article.copy(isBookmarked = !article.isBookmarked)
        viewModelScope.launch {
            updateBookmark(updatedArticle)
        }
    }


}