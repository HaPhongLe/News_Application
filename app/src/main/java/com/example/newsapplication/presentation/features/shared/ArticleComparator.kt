package com.example.newsapplication.presentation.features.shared

import androidx.recyclerview.widget.DiffUtil
import com.example.newsapplication.domain.model.Article

class ArticleComparator: DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.url == newItem.url
    }
}