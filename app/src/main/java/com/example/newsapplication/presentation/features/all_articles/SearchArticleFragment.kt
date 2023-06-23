package com.example.newsapplication.presentation.features.all_articles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsapplication.AppBarSetting
import com.example.newsapplication.R

class SearchArticleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppBarSetting).setTtitle(getString(R.string.title_search_articles))

        return inflater.inflate(R.layout.fragment_search_article, container, false)
    }

}