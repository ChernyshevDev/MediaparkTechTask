package com.example.mediaparktechtask.presentation.news_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mediaparktechtask.domain.entity.NewsCard
import com.example.mediaparktechtask.domain.contract.NewsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewsPageViewModel @Inject constructor(
    private val newsProvider: NewsProvider
) : ViewModel() {

    var news: MutableLiveData<List<NewsCard>> = MutableLiveData()

    init {
        fetchNews()
    }

    private fun fetchNews() {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val listOfNews = newsProvider.getTopNews()
                news.postValue(listOfNews)
            }
        } catch (noInternetException: Exception) {
        }
    }
}