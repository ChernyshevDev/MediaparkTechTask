package com.example.mediaparktechtask.data.retrofit

import com.example.mediaparktechtask.domain.entity.NewsCard

data class NewsResponse(
    val articles: List<Article>,
    val totalArticles: Int
) {
    data class Article(
        val content: String,
        val description: String,
        val image: String,
        val publishedAt: String,
        val source: Source,
        val title: String,
        val url: String
    )

    data class Source(
        val name: String,
        val url: String
    )
}

fun NewsResponse.toListOfNews(): List<NewsCard> {
    val newsList = mutableListOf<NewsCard>()
    for (news in this.articles) {
        newsList.add(
            NewsCard(
                title = news.title,
                content = news.description,
                imageLink = news.image,
                sourceLink = news.url
            )
        )
    }
    return newsList
}
