package com.example.newsapplication.presentation.features.headlines

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentBreakingNewsBinding
import com.example.newsapplication.presentation.shared.NewsRecyclerViewListAdapter
import com.example.newsapplication.presentation.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {

    private val TAG = "BreakingNewsFrag"

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
        val recyclerViewListAdapter = NewsRecyclerViewListAdapter(viewModel::onBookmarkClick)
        binding.apply {
            newsContainer.adapter = recyclerViewListAdapter
            newsContainer.layoutManager = LinearLayoutManager(requireContext())

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.state.collect{
                        Log.d(TAG, "onViewCreated: ${it.status}")
                        swipeRefreshLayout.isRefreshing = it.status == Status.LOADING

                        if(it.status != Status.ERROR){
                            if(it.status == Status.SUCCESS){
                                if (viewModel.scrollTotop){
                                    newsContainer.scrollToPosition(0)
                                }
                                viewModel.scrollTotop = false
                            }
                            recyclerViewListAdapter.submitList(it.data)
                        }else  {
                            showSnackbar(
                                message = it.error?: "Unknown Error"
                            )
                        }

                    }
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
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

    override fun onStart() {
        super.onStart()
        viewModel.autoRefresh()
    }

}