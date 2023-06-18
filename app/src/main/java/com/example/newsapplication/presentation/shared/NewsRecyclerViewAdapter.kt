package com.example.newsapplication.presentation.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.newsapplication.R
import com.example.newsapplication.databinding.NewsViewBinding
import com.example.newsapplication.domain.model.Article

class NewsRecyclerViewListAdapter(
    val onBookmark: (Article) -> Unit
): ListAdapter<Article, ArticleViewHolder>(ArticleComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsViewBinding.inflate(layoutInflater, parent, false)
        return  ArticleViewHolder(
            binding = binding,
            onBookmarkClick = onBookmark)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}