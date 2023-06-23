package com.example.newsapplication.presentation.features.bookmark

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.use_case.ResetAllBookmarks
import com.example.newsapplication.domain.use_case.GetBookMarks
import com.example.newsapplication.domain.use_case.UpdateBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookMarksUsecase: GetBookMarks,
    private val updateBookmarkUsecase: UpdateBookmark,
    private val resetAllBookmarksUsecase: ResetAllBookmarks
): ViewModel() {
    private val TAG = "BookmarkViewModel"

    private val _bookmarks = MutableStateFlow<List<Article>>(emptyList())
    val bookmarks: StateFlow<List<Article>> = _bookmarks

    init{
        viewModelScope.launch{
            Log.d(TAG, "getBookmarks: ${getBookMarksUsecase.invoke()}")
            getBookMarksUsecase().collect{ articles ->
                _bookmarks.value = articles
            }
        }
    }

    fun  updateBookmark(article: Article){
        viewModelScope.launch {
            Log.d(TAG, "updateBookmark: ${article.isBookmarked}")
            val updatedArticle = article.copy(isBookmarked = !article.isBookmarked)
            updateBookmarkUsecase(updatedArticle)
        }
    }

    fun resetAllBookmarks(){
        viewModelScope.launch {
            resetAllBookmarksUsecase()
        }
    }
}