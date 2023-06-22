package com.example.newsapplication.presentation.features.bookmark

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.domain.model.Article
import com.example.newsapplication.domain.use_case.GetBookMarks
import com.example.newsapplication.domain.use_case.UpdateBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookMarksUsecase: GetBookMarks,
    private val updateBookmarkUsecase: UpdateBookmark
): ViewModel() {
    private val TAG = "BookmarkViewModel"

    private val _bookmarks = MutableLiveData<List<Article>>(emptyList())
    val bookmarks: LiveData<List<Article>> = _bookmarks

    fun getBookmarks(){
        viewModelScope.launch{
            Log.d(TAG, "getBookmarks: ${getBookMarksUsecase.invoke()}")
            getBookMarksUsecase().collect{ articles ->
                _bookmarks.value = articles
            }
        }
    }

    fun updateBookmark(article: Article){
        viewModelScope.launch {
            updateBookmarkUsecase(article)
        }
    }
}