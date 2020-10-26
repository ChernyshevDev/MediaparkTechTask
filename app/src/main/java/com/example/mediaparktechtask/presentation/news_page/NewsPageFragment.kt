package com.example.mediaparktechtask.presentation.news_page

import android.content.Context
import android.graphics.Bitmap
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
import com.example.mediaparktechtask.presentation.expanded_news.TITLE
import com.example.mediaparktechtask.presentation.adapters.MultiAdapter
import com.example.mediaparktechtask.presentation.adapters.Item
import com.example.mediaparktechtask.presentation.expanded_news.WEB_LINK
import com.example.mediaparktechtask.presentation.search_page.toNewsItems
import dagger.android.support.AndroidSupportInjection
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class NewsPageFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: NewsPageViewModel
    private lateinit var binding: FragmentNewsBinding

    @Inject
    lateinit var adapter: MultiAdapter

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
            .of(this, viewModelFactory)[NewsPageViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        setUpRecyclerViewItems()
        setUpRecyclerView()
    }

    private fun setUpViews() {
        with(binding) {
            newsHeaderText.text = getString(R.string.navigation_header_news)
        }
    }

    private fun setUpRecyclerView() {
        adapter.setOnItemClickListener {
            with(it as Item.NewsItem) {
                expandSelectedNews(it)
            }
        }
        binding.newsFeedRecycler.adapter = adapter
    }

    private fun expandSelectedNews(newsItem: Item.NewsItem) {
        if (findNavController().currentDestination?.id == R.id.news_page) {
            findNavController().navigate(R.id.action_news_page_to_expanded_news,
                Bundle().apply {
                    putByteArray(IMAGE, newsItem.news.image?.toByteArray())
                    putString(TITLE, newsItem.news.title)
                    putString(CONTENT, newsItem.news.content)
                    putString(WEB_LINK, newsItem.news.sourceLink)
                })
        }
    }

    private fun setUpRecyclerViewItems() {
        viewModel.news.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items.toNewsItems())
            disableLoadingAnimation()
        }
    }

    private fun disableLoadingAnimation() {
        with(binding) {
            newsLoadingAnimation.it.visibility = View.GONE
        }
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}