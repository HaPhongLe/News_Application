package com.example.newsapplication.presentation.shared

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.newsapplication.databinding.NewsViewBinding
import com.example.newsapplication.domain.model.Article

class ArticleViewHolder(private val binding: NewsViewBinding): ViewHolder(binding.root) {

    fun bind(article: Article){
        binding.newsTitle.text = article.title
        binding.newsSource.text = article.source
        binding.newsDescription.text = article.description
        Glide.with(binding.root)
            .load(article.urlToImage)
            .into(binding.newsThumbnail)
    }
}