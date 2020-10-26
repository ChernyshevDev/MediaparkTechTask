package com.example.mediaparktechtask.domain.contract

import android.graphics.Bitmap

interface ImageDownloader {
    suspend fun getBitmapFromLink(link: String): Bitmap
}