package com.example.newsapplication.presentation.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.newsapplication.R
import com.example.newsapplication.databinding.NewsLoadStateAdapterBinding

class NewsLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<NewsLoadStateAdapter.NewsLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: NewsLoadStateViewHolder, loadState: LoadState) {
       holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NewsLoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.news_load_state_adapter, parent, false)
        val binding =  NewsLoadStateAdapterBinding.bind(view)
        return NewsLoadStateViewHolder(binding, retry)
    }

    inner class NewsLoadStateViewHolder(private val binding: NewsLoadStateAdapterBinding, private val retry: () -> Unit): ViewHolder(binding.root){
        init {
            binding.btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState){
            binding.apply {
                if (loadState is LoadState.Error){
                    errorMsg.text = loadState.error.localizedMessage
                }
                progressBar.isVisible = loadState is LoadState.Loading
                btnRetry.isVisible = loadState is LoadState.Error
                errorMsg.isVisible = loadState is LoadState.Error
            }
        }
    }
}