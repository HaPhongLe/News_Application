package com.example.newsapplication.presentation.shared

import android.util.Log
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.newsapplication.R
import com.example.newsapplication.databinding.NewsViewBinding
import com.example.newsapplication.domain.model.Article

class ArticleViewHolder(
    private val binding: NewsViewBinding,
    private val onBookmarkClick: (Article) -> Unit
    ): ViewHolder(binding.root) {

    fun bind(article: Article){
        binding.apply {
            newsTitle.text = article.title
            newsSource.text = article.source
            newsDescription.text = article.description
            newsBookmark.setImageResource(
                if(article.isBookmarked){
                    R.drawable.baseline_bookmark_24
                }else{
                    R.drawable.baseline_bookmark_border_24
                }
            )
            Log.d("viewholder", "bind: ${article.urlToImage}")
            Glide.with(binding.root)
                .load(article.urlToImage)
                .placeholder(R.drawable.place_holder)
                .into(binding.newsThumbnail)


            newsBookmark.setOnClickListener {
                onBookmarkClick(article)
            }
        }
    }
}