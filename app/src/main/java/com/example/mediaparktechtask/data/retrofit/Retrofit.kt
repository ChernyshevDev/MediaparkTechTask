package com.example.mediaparktechtask.data.retrofit

import com.example.mediaparktechtask.data.containers.SearchFilter
import com.example.mediaparktechtask.data.containers.SearchOptions
import com.example.mediaparktechtask.data.containers.SortOptions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.mediaparktechtask.data.containers.Date

const val apiKey = "e9ac1fc472d16cd69b9628212ca2db93"
const val url = "https://gnews.io"
const val LANGUAGE_ENGLISH = "en"

interface NewsRetrofit {
    @GET("/api/v4/top-headlines?")
    fun getTopNews(
        @Query("token") token: String = apiKey,
        @Query("lang") language: String = LANGUAGE_ENGLISH
    ): Call<NewsResponse>

    @GET("/api/v4/search?")
    fun getNews(
        @Query("q") keyword: String,
        @Query("lang") language: String = LANGUAGE_ENGLISH,
        @Query("in") searchIn: String? = null,
        @Query("from") searchFrom: String? = null,
        @Query("to") searchTo: String? = null,
        @Query("sortby") sortBy: String? = null,
        @Query("token") token: String = apiKey
    ): Call<NewsResponse>
}

fun NewsRetrofit.getFilteredNews(keyword: String, sortOptions: SortOptions?, filter: SearchFilter?)
        : Call<NewsResponse> {
    return getNews(
        keyword = keyword,
        searchIn = filter?.searchIn?.toRequest(),
        searchFrom = filter?.dateFrom?.toRequestDate(),
        searchTo = filter?.dateTo?.toRequestDate(),
        sortBy = sortOptions?.toRequest(),
    )
}

private fun Date.toRequestDate(): String {
    // time format: 2020-10-24T19:27:26Z
    var string = ""
    string += "$year-"
    if (month < 10) string += "0"
    string += "$month-"
    if (day < 10) string += "0"
    string += "$day"
    return string + "T00:00:00Z"
}

private fun List<SearchOptions>.toRequest(): String {
    var string = ""
    for (option in this) {
        string += option.toWord() + ","
    }
    return string.substring(0, string.length - 1)
}

private fun SearchOptions.toWord(): String =
    when (this) {
        is SearchOptions.SearchInContent -> "content"
        is SearchOptions.SearchInDescription -> "description"
        is SearchOptions.SearchInTitle -> "title"
    }

private fun SortOptions.toRequest(): String? {
    return when (this) {
        is SortOptions.SortByRelevance -> "relevance"
        is SortOptions.SortByUploadDate -> "publishedAt"
    }
}