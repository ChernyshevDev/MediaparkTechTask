package com.example.mediaparktechtask.data.containers

import androidx.lifecycle.MutableLiveData

/**
 * Search bar and filter buttons located in MainActivity layout, however
 * keywords to search/filters/etc. should be processed in specific fragments.
 * For passing data between them I use this object with LiveData data container.
 */
object SearchDataContainer {
    val data: MutableLiveData<SearchBarData> = MutableLiveData()

    init {
        data.value = SearchBarData()
    }
}

data class SearchBarData(
    var inputText: String? = null,
    var searchHistory: MutableList<String> = mutableListOf(),
    var filter: SearchFilter = SearchFilter(),
    var sortBy: SortOptions = SortOptions.SortByUploadDate,
    var filtersApplied: Int = 2
)

data class SearchFilter(
    var dateFrom: Date? = null,
    var dateTo: Date? = null,
    var searchIn: MutableList<SearchOptions> = arrayListOf(
        SearchOptions.SearchInTitle,
        SearchOptions.SearchInDescription
    )
)

data class Date(
    val year: Int,
    val month: Int,
    val day: Int
)

sealed class SearchOptions {
    object SearchInTitle : SearchOptions()
    object SearchInDescription : SearchOptions()
    object SearchInContent : SearchOptions()
}

sealed class SortOptions {
    object SortByUploadDate : SortOptions()
    object SortByRelevance : SortOptions()
}

fun calculateFilters(searchBarData: SearchBarData): Int {
    var filters: Int = 0
    with(searchBarData) {
        if (filter.dateFrom != null) filters++
        if (filter.dateTo != null) filters++
        filters += filter.searchIn.size
        filtersApplied = filters
    }
    return filters
}