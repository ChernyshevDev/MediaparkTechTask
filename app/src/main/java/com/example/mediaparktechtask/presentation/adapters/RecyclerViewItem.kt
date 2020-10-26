package com.example.mediaparktechtask.presentation.adapters

import com.example.mediaparktechtask.domain.entity.NewsCard

data class RecyclerViewItem(
    val item: Item,
    val itemType: Int = item.type
)

sealed class Item(val type: Int) {

    companion object {
        const val TYPE_NEWS: Int = 1
        const val TYPE_LAST_SEARCHES: Int = 2
    }

    data class NewsItem(
        val news: NewsCard
    ) : Item(TYPE_NEWS)

    data class LastSearchesItem(
        val title: String
    ) : Item(TYPE_LAST_SEARCHES)
}