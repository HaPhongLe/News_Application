package com.example.newsapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.newsapplication.presentation.features.all_articles.AllArticleViewModel
import com.example.newsapplication.presentation.features.headlines.BreakingNewsFragment
import com.example.newsapplication.presentation.features.headlines.BreakingNewsViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: AllArticleViewModel by lazy {
        ViewModelProvider(this).get(AllArticleViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        viewModel.getArticles()
        openBreakingNewsFrag()
    }

    private fun openBreakingNewsFrag(){
        supportFragmentManager.beginTransaction().replace(R.id.frag_container, BreakingNewsFragment()).commit()
    }
}