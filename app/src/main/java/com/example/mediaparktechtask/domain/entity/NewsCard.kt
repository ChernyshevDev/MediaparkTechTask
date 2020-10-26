package com.example.mediaparktechtask.domain.entity

import android.graphics.Bitmap

data class NewsCard(
    val imageLink: String,
    var image: Bitmap? = null,
    val title: String,
    val content: String,
    val sourceLink: String
)