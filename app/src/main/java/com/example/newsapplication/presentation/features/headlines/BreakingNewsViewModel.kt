package com.example.newsapplication.presentation.features.headlines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.domain.use_case.GetBreakingNews
import com.example.newsapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val getBreakingNews: GetBreakingNews
): ViewModel() {
    
    private val TAG = "GetBreakingNews"

    private val _state = MutableLiveData<BreakingNewsState>(BreakingNewsState(status = Status.LOADING, data = emptyList()))
    val state: LiveData<BreakingNewsState> = _state

    fun getHeadlines(){
        viewModelScope.launch { 
            getBreakingNews().collect{result ->
                Log.d(TAG, "getHeadlines: ${result.data} ${result.error}")
                when(result){
                    is Resource.Loading -> {
                        _state.value = BreakingNewsState(Status.LOADING, data = result.data?: emptyList())
                    }
                    is Resource.Success -> {
                        _state.value = BreakingNewsState(Status.SUCCESS, data = result.data?: emptyList())
                    }
                    is Resource.Error -> {
                        _state.value = BreakingNewsState(Status.ERROR, data = result.data?: emptyList(), error = result.error?.message?: "Unkown Error")
                    }
                }
            }
        }
    }
}