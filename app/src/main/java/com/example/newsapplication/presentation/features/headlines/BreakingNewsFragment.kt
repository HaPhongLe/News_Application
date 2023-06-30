package com.example.newsapplication.presentation.features.headlines

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.newsapplication.AppBarSetting
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentBreakingNewsBinding
import com.example.newsapplication.presentation.shared.NewsArticlePagingAdapter
import com.example.newsapplication.presentation.shared.NewsRecyclerViewListAdapter
import com.example.newsapplication.presentation.util.setInvisibility
import com.example.newsapplication.presentation.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {

    private val TAG = "BreakingNewsFrag"

    private val viewModel: BreakingNewsViewModel by viewModels()

    private lateinit var binding: FragmentBreakingNewsBinding

    private val openArticleInBrowser = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        //no action needed
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppBarSetting).setTtitle(getString(R.string.title_breaking_news))
        binding = FragmentBreakingNewsBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewListAdapter = NewsArticlePagingAdapter(
            onBookmark = { article ->
                Log.d(TAG, "onViewCreated: bookmark click")
                viewModel.onBookmarkClick(article)
            } ,
            onItemClick = {article ->
                Log.d(TAG, "onViewCreated: itemview click")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                openArticleInBrowser.launch(intent)
            }
            )
        binding.apply {
            newsContainer.apply {
                adapter = recyclerViewListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                itemAnimator?.changeDuration =0
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
//                    viewModel.state.collect{
//                        Log.d(TAG, "onViewCreated: ${it.status} ${viewModel.scrollTotop}")
//                        swipeRefreshLayout.isRefreshing = it.status == Status.LOADING
//
//                        if(it.status != Status.ERROR){
//                            recyclerViewListAdapter.submitData(it.data)
//                            if(it.status == Status.SUCCESS){
//                                if (viewModel.scrollTotop){
//                                    Log.d(TAG, "onViewCreated: scroll to position 0")
//                                    newsContainer.scrollToPosition(0)
//                                }
//                                viewModel.scrollTotop = false
//                            }
//
//                        }else  {
//                            showSnackbar(
//                                message = it.error?: getString(R.string.error_unknown)
//                            )
//                        }
//
//                    }
                    viewModel.breakingNews.collectLatest { pagingData ->
                        recyclerViewListAdapter.submitData(pagingData)
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    recyclerViewListAdapter.loadStateFlow.collect{ loadState ->
                        Log.d(TAG, "onViewCreated: $loadState")
                        when( val refresh = loadState.mediator?.refresh){
                            is LoadState.NotLoading -> {
                                swipeRefreshLayout.isRefreshing = false
                                breakingNewsError.isVisible = false
                                btnRefresh.isVisible = false
                                newsContainer.isVisible = recyclerViewListAdapter.itemCount > 0
                            }
                            is LoadState.Loading -> {
                                swipeRefreshLayout.isRefreshing = true
                                breakingNewsError.isVisible = false
                                btnRefresh.isVisible = false
                                newsContainer.setInvisibility(true)
                            }
                            is LoadState.Error -> {
                                swipeRefreshLayout.isRefreshing = false
                                newsContainer.isVisible = recyclerViewListAdapter.itemCount > 0
                                val noCacheResults = recyclerViewListAdapter.itemCount < 1 && loadState.source.append.endOfPaginationReached

                                breakingNewsError.isVisible = noCacheResults
                                breakingNewsError.text = refresh.error?.localizedMessage ?: "Unknown Error"

                                btnRefresh.isVisible = noCacheResults

                            }

                            else -> {

                            }
                        }
                    }
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.manualRefresh()
            }
            btnRefresh.setOnClickListener {
                viewModel.manualRefresh()
            }

            val menuHost = requireActivity() as MenuHost
            menuHost.addMenuProvider(
                object : MenuProvider{
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        menuInflater.inflate(R.menu.breaking_news_menu, menu)
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                        return when(menuItem.itemId){
                            R.id.refresh -> {
                                viewModel.manualRefresh()
                                true
                            }
                            else ->{
                                true
                            }
                        }
                    }
                }, viewLifecycleOwner, Lifecycle.State.RESUMED
            )
        }


    }

//    override fun onStart() {
//        super.onStart()
//        viewModel.autoRefresh()
//    }

}