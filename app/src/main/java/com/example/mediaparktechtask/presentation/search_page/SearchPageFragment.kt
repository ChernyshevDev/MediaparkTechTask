package com.example.mediaparktechtask.presentation.search_page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.databinding.FragmentNewsBinding
import com.example.mediaparktechtask.presentation.expanded_news.CONTENT
import com.example.mediaparktechtask.presentation.expanded_news.IMAGE
import com.example.mediaparktechtask.data.containers.SearchDataContainer
import com.example.mediaparktechtask.data.containers.updateState
import com.example.mediaparktechtask.presentation.ClickEventHelper
import com.example.mediaparktechtask.presentation.expanded_news.TITLE
import com.example.mediaparktechtask.presentation.adapters.MultiAdapter
import com.example.mediaparktechtask.presentation.adapters.Item
import com.example.mediaparktechtask.presentation.expanded_news.WEB_LINK
import com.example.mediaparktechtask.presentation.news_page.toByteArray
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SearchPageFragment : Fragment() {

    @Inject
    lateinit var adapter: MultiAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchPageViewModel

    private lateinit var binding: FragmentNewsBinding

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater)
        viewModel = ViewModelProviders
            .of(this, viewModelFactory)[SearchPageViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupViews()
        setUpRecyclerView()

        SearchDataContainer.data.observe(viewLifecycleOwner) {
            if (it.inputText != null) {
                viewModel.fetchNews(
                    keyword = it.inputText!!,
                    sortBy = it.sortBy,
                    filter = it.filter
                )
                clearInputText()
            }
            ClickEventHelper.setCountOfFilters(it.filtersApplied)
        }
    }

    private fun clearInputText() {
        SearchDataContainer.updateState {
            inputText = null
        }
    }

    private fun setupViewModel() {
        viewModel.showLoadingAnimation =
            { binding.newsLoadingAnimation.it.visibility = View.VISIBLE }
        viewModel.hideLoadingAnimation = { binding.newsLoadingAnimation.it.visibility = View.GONE }
    }

    private fun setupViews() {
        hideLoadingAnimation()
    }

    private fun hideLoadingAnimation() {
        binding.newsLoadingAnimation.root.visibility = View.GONE
    }

    private fun onNewsItemClick(item: Item.NewsItem) {
        openExpandedNewsPage(item)
    }

    private fun openExpandedNewsPage(item: Item.NewsItem) {
        if (findNavController().currentDestination?.id == R.id.search_page) {
            findNavController().navigate(
                R.id.action_search_page_to_expanded_news,
                Bundle().apply {
                    putByteArray(IMAGE, item.news.image?.toByteArray())
                    putString(TITLE, item.news.title)
                    putString(CONTENT, item.news.content)
                    putString(WEB_LINK, item.news.sourceLink)
                })
        }
    }

    private fun onLastSearchesClick(item: Item.LastSearchesItem) {
        fetchNews(item.title)
    }

    private fun fetchNews(keyword: String) {
        SearchDataContainer.updateState {
            inputText = keyword
        }
    }

    private fun setUpRecyclerView() {
        adapter.setOnItemClickListener { item ->
            when (item) {
                is Item.NewsItem -> onNewsItemClick(item)
                is Item.LastSearchesItem -> onLastSearchesClick(item)
            }
        }

        viewModel.viewData.observe(viewLifecycleOwner) {
            adapter.setItems(viewModel.viewData.value!!.recyclerViewItems)
            binding.newsHeaderText.text = viewModel.viewData.value!!.title
        }

        binding.newsFeedRecycler.adapter = adapter
    }
}