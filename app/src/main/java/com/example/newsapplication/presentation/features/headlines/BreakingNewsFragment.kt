package com.example.newsapplication.presentation.features.headlines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentBreakingNewsBinding
import com.example.newsapplication.presentation.shared.NewsRecyclerViewListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {

    private val viewModel: BreakingNewsViewModel by viewModels()

    private lateinit var binding: FragmentBreakingNewsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBreakingNewsBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHeadlines()
        val recyclerViewListAdapter = NewsRecyclerViewListAdapter(viewModel::onBookmarkClick)
        binding.apply {
            newsContainer.adapter = recyclerViewListAdapter
            newsContainer.layoutManager = LinearLayoutManager(requireContext())
            viewModel.state.observe(this@BreakingNewsFragment){
                swipeRefreshLayout.isRefreshing = it.status == Status.LOADING
                refreshBtn.isVisible = it.status == Status.ERROR
                breakingNewsError.isVisible = it.status == Status.ERROR
                breakingNewsError.text = it.error

                if(it.status == Status.SUCCESS){
                    recyclerViewListAdapter.submitList(it.data)
                }
            }
        }


    }
}