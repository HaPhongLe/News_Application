package com.example.newsapplication.presentation.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import com.example.newsapplication.R
import com.example.newsapplication.databinding.NewsViewBinding
import com.example.newsapplication.domain.model.Article

class NewsArticlePagingAdapter(
    val onBookmark: (Article) -> Unit,
    val onItemClick: (Article) -> Unit
): PagingDataAdapter<Article, ArticleViewHolder>(ArticleComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsViewBinding.inflate(layoutInflater, parent, false)
        return  ArticleViewHolder(
            binding = binding,
            onBookmarkClick = {position ->
                getItem(position)?.let { onBookmark(it) }
            },
            onItemClick = {position ->
                getItem(position)?.let { onItemClick(it) }
            }
            )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}