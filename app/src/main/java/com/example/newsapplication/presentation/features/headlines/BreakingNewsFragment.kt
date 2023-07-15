package com.example.newsapplication.presentation.features.headlines

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.AppBarSetting
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentBreakingNewsBinding
import com.example.newsapplication.presentation.shared.NewsArticlePagingAdapter
import com.example.newsapplication.presentation.shared.NewsLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {

    private val TAG = "BreakingNewsFrag"

    private val viewModel: BreakingNewsViewModel by viewModels()

    private lateinit var binding: FragmentBreakingNewsBinding

    private val openArticleInBrowser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //no action needed
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppBarSetting).setTtitle(getString(R.string.title_breaking_news))
        binding = FragmentBreakingNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewListAdapter = NewsArticlePagingAdapter(
            onBookmark = { article ->
                Log.d(TAG, "onViewCreated: bookmark click")
                viewModel.onBookmarkClick(article)
            },
            onItemClick = { article ->
                Log.d(TAG, "onViewCreated: itemview click")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                openArticleInBrowser.launch(intent)
            }
        )
        binding.apply {
            newsContainer.apply {
                adapter = recyclerViewListAdapter
                    .withLoadStateHeaderAndFooter(
                        header = NewsLoadStateAdapter { recyclerViewListAdapter.retry() },
                        footer = NewsLoadStateAdapter { recyclerViewListAdapter.retry() }
                    )
                layoutManager = LinearLayoutManager(requireContext())
                itemAnimator?.changeDuration = 0
            }

            var isEndOfPaginationReached = false
            var isTheLastItemVisible = false
            newsContainer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        (newsContainer.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    isTheLastItemVisible =
                        lastVisibleItemPosition >= recyclerViewListAdapter.itemCount - 1

                    Log.d(TAG, "onScrolled: $isTheLastItemVisible")
                    endOfList.isVisible = isEndOfPaginationReached && isTheLastItemVisible
                }
            })

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {

                    viewModel.breakingNews.collectLatest { pagingData ->
                        recyclerViewListAdapter.submitData(pagingData)
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    recyclerViewListAdapter.loadStateFlow.collect { loadState ->
                        Log.d(TAG, "onViewCreated: $loadState")

                        swipeRefreshLayout.isRefreshing =
                            loadState.refresh is LoadState.Loading // || loadState.mediator?.refresh is LoadState.Loading
                        btnRetry.isVisible =
                            loadState.mediator?.refresh is LoadState.Error && recyclerViewListAdapter.itemCount < 1
                        newsContainer.isVisible =
                            loadState.source.refresh !is LoadState.Loading && loadState.mediator?.refresh !is LoadState.Loading

                        val errorStateRefresh = loadState.mediator?.refresh as? LoadState.Error
                            ?: loadState.refresh as? LoadState.Error

                        breakingNewsError.isVisible =
                            errorStateRefresh != null && recyclerViewListAdapter.itemCount == 0
                        breakingNewsError.text =
                            errorStateRefresh?.error?.localizedMessage ?: "Unknown Error"

                        if (errorStateRefresh != null && recyclerViewListAdapter.itemCount > 0) {
                            errorStateRefresh.error.localizedMessage?.let { sendErrorToast(it) }
                        }

                        val errorStateAppendOrPrepend = loadState.source.append as? LoadState.Error
                            ?: loadState.source.prepend as? LoadState.Error
                            ?: loadState.append as? LoadState.Error
                            ?: loadState.prepend as? LoadState.Error

                        if (errorStateAppendOrPrepend != null) {
                            errorStateAppendOrPrepend.error.localizedMessage?.let {
                                sendErrorToast(
                                    it
                                )
                            }
                        }

                        Log.d(TAG, "onViewCreated: $isTheLastItemVisible ${loadState.append.endOfPaginationReached}")
                        isEndOfPaginationReached = loadState.append.endOfPaginationReached && loadState.refresh != LoadState.Loading

//                        when( val refresh = loadState.mediator?.refresh){
//                            is LoadState.NotLoading -> {
//                                swipeRefreshLayout.isRefreshing = false
//                                breakingNewsError.isVisible = false
//                                btnRefresh.isVisible = false
//                                newsContainer.isVisible = recyclerViewListAdapter.itemCount > 0
//                            }
//                            is LoadState.Loading -> {
//                                swipeRefreshLayout.isRefreshing = true
//                                breakingNewsError.isVisible = false
//                                btnRefresh.isVisible = false
//                                newsContainer.setInvisibility(true)
//                            }
//                            is LoadState.Error -> {
//                                swipeRefreshLayout.isRefreshing = false
//                                newsContainer.isVisible = recyclerViewListAdapter.itemCount > 0
//                                val noCacheResults = recyclerViewListAdapter.itemCount < 1 && loadState.source.append.endOfPaginationReached
//
//                                breakingNewsError.isVisible = noCacheResults
//                                breakingNewsError.text = refresh.error?.localizedMessage ?: "Unknown Error"
//
//                                btnRefresh.isVisible = noCacheResults
//
//                            }
//
//                            else -> {
//
//                            }
//                        }
                    }
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.manualRefresh()
            }
            btnRetry.setOnClickListener {
                viewModel.manualRefresh()
            }

            val menuHost = requireActivity() as MenuHost
            menuHost.addMenuProvider(
                object : MenuProvider {
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        menuInflater.inflate(R.menu.breaking_news_menu, menu)
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                        return when (menuItem.itemId) {
                            R.id.refresh -> {
                                viewModel.autoRefresh()
                                true
                            }
                            else -> {
                                true
                            }
                        }
                    }
                }, viewLifecycleOwner, Lifecycle.State.RESUMED
            )
        }


    }

    override fun onStart() {
        super.onStart()
        viewModel.autoRefresh()
    }

    private fun sendErrorToast(error: String) {
        Toast.makeText(
            context,
            "\\uD83D\\uDE28 Wooops $error",
            Toast.LENGTH_LONG
        ).show()
    }

}