package com.example.mediaparktechtask.domain.contract

import com.example.mediaparktechtask.domain.entity.NewsCard
import com.example.mediaparktechtask.data.containers.SearchFilter
import com.example.mediaparktechtask.data.containers.SortOptions

interface NewsProvider {
    fun getTopNews(): List<NewsCard>

    fun getNews(
        keyword: String,
        sortBy: SortOptions? = null,
        filter: SearchFilter? = null,
    ): List<NewsCard>
}