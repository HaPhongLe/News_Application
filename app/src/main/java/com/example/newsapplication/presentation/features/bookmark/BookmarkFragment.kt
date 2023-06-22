package com.example.newsapplication.presentation.features.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentBookmarkBinding
import com.example.newsapplication.presentation.features.headlines.BreakingNewsViewModel
import com.example.newsapplication.presentation.shared.NewsRecyclerViewListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkFragment : Fragment() {

    private val viewModel: BookmarkViewModel by viewModels()
    private lateinit var binding: FragmentBookmarkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getBookmarks()
        val bookmarkRecycleViewAdapter = NewsRecyclerViewListAdapter(
            onBookmark = viewModel::updateBookmark
        )
        binding.bookmarksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bookmarksRecyclerView.adapter = bookmarkRecycleViewAdapter
        viewModel.bookmarks.observe(this){
            bookmarkRecycleViewAdapter.submitList(it)
        }
    }

}