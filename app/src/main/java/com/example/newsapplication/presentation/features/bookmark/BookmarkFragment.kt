package com.example.newsapplication.presentation.features.bookmark

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapplication.AppBarSetting
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentBookmarkBinding
import com.example.newsapplication.presentation.shared.NewsRecyclerViewListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarkFragment : Fragment() {

    private val viewModel: BookmarkViewModel by viewModels()
    private lateinit var binding: FragmentBookmarkBinding

    private val openArticleInBrowser = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        //do nothing
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppBarSetting).setTtitle(getString(R.string.title_bookmarks))
        binding = FragmentBookmarkBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookmarkRecycleViewAdapter = NewsRecyclerViewListAdapter(
            onBookmark = {article ->
                viewModel.updateBookmark(article)
            },
            onItemClick = {article ->
                openArticleInBrowser.launch(Intent(Intent.ACTION_VIEW, Uri.parse(article.url)))
            }
        )

        binding.apply {
            bookmarksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            bookmarksRecyclerView.adapter = bookmarkRecycleViewAdapter
        }


        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.bookmarks.collect{
                    bookmarkRecycleViewAdapter.submitList(it)
                }
            }
        }


        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.bookmarks_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                viewModel.resetAllBookmarks()
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

}