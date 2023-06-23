package com.example.newsapplication

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsapplication.databinding.ActivityMainBinding
import com.example.newsapplication.presentation.features.all_articles.AllArticleViewModel
import com.example.newsapplication.presentation.features.headlines.BreakingNewsFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppBarSetting{
    private val TAG = "MainActivity"

    private val viewModel: AllArticleViewModel by lazy {
        ViewModelProvider(this).get(AllArticleViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun setTtitle(title: String) {
        supportActionBar?.title = title
    }
}

interface AppBarSetting{
    fun setTtitle(title: String)
}