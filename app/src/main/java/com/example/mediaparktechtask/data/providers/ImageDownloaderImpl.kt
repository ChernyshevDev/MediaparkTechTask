package com.example.mediaparktechtask.data.providers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.domain.contract.ImageDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

class ImageDownloaderImpl @Inject constructor(
    private val context: Context
) : ImageDownloader {
    override suspend fun getBitmapFromLink(link: String): Bitmap {
        return try {
            var inputStream: InputStream
            var image: Bitmap? = null
            withContext(Dispatchers.IO) {
                inputStream = URL(link).content as InputStream
                image = BitmapFactory.decodeStream(inputStream)
            }
            image!!
        } catch (e: Exception) {
            BitmapFactory
                .decodeResource(
                    context.resources,
                    R.drawable.no_image_available_img
                )
        }
    }
}