package com.example.mediaparktechtask.presentation.search_page

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.domain.contract.NewsProvider
import com.example.mediaparktechtask.domain.entity.NewsCard
import com.example.mediaparktechtask.data.containers.SearchDataContainer
import com.example.mediaparktechtask.data.containers.SearchFilter
import com.example.mediaparktechtask.data.containers.SortOptions
import com.example.mediaparktechtask.presentation.adapters.Item
import com.example.mediaparktechtask.presentation.adapters.RecyclerViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SearchPageViewModel @Inject constructor(
    private val newsProvider: NewsProvider,
    private val context: Context
) : ViewModel() {
    val viewData: MutableLiveData<SearchPageViewState> = MutableLiveData()
    lateinit var showLoadingAnimation: () -> Unit
    lateinit var hideLoadingAnimation: () -> Unit

    init {
        viewData.value = SearchPageViewState(
            title = context.getString(R.string.search_history),
            recyclerViewItems = SearchDataContainer.data
                .value!!.searchHistory.toLastSearchesItems()
        )
    }

    fun fetchNews(keyword: String, sortBy: SortOptions?, filter: SearchFilter?) {
        try {
            showLoadingAnimation()
            GlobalScope.launch(Dispatchers.IO) {
                val listOfNews = newsProvider.getNews(keyword, sortBy, filter)
                viewData.postValue(viewData.value!!.apply {
                    title = String.format(
                        context.getString(R.string.count_of_news), listOfNews.size
                    )
                    recyclerViewItems = listOfNews.toNewsItems()
                })
            }
            hideLoadingAnimation()
        } catch (e: Exception) {

        }
    }
}

data class SearchPageViewState(
    var title: String,
    var recyclerViewItems: MutableList<RecyclerViewItem>
)

fun List<NewsCard>.toNewsItems(): MutableList<RecyclerViewItem> {
    val listOfItems = mutableListOf<RecyclerViewItem>()
    for (news in this) {
        listOfItems.add(
            RecyclerViewItem(
                Item.NewsItem(
                    news
                )
            )
        )
    }
    return listOfItems
}

fun List<String>.toLastSearchesItems(): MutableList<RecyclerViewItem> {
    val listOfItems = mutableListOf<RecyclerViewItem>()
    for (item in this) {
        listOfItems.add(
            RecyclerViewItem(
                Item.LastSearchesItem(
                    item
                )
            )
        )
    }
    return listOfItems
}
