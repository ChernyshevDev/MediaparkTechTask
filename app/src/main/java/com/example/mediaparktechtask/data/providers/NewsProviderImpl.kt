package com.example.mediaparktechtask.data.providers

import com.example.mediaparktechtask.data.retrofit.*
import com.example.mediaparktechtask.domain.contract.NewsProvider
import com.example.mediaparktechtask.domain.entity.NewsCard
import com.example.mediaparktechtask.data.containers.SearchFilter
import com.example.mediaparktechtask.data.containers.SortOptions
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class NewsProviderImpl @Inject constructor() : NewsProvider {
    override fun getTopNews(): List<NewsCard> {
        val retrofit = getRetrofit()
        val response = retrofit
            .getTopNews()
            .makeRequest()

        return response!!.toListOfNews()
    }

    override fun getNews(
        keyword: String,
        sortBy: SortOptions?,
        filter: SearchFilter?
    ): List<NewsCard> {
        try{
            val retrofit = getRetrofit()
            val response = retrofit
                .getFilteredNews(
                    keyword = keyword,
                    sortOptions = sortBy,
                    filter = filter
                )
                .makeRequest()
            return response!!.toListOfNews()
        } catch (e: Exception){
         throw Exception("Unable to fetch news. Your gnews request limit should be exceed.")
        }
    }

    private fun getRetrofit(): NewsRetrofit {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(NewsRetrofit::class.java)
    }

    private fun Call<NewsResponse>.makeRequest() =
        this.execute().body()
}