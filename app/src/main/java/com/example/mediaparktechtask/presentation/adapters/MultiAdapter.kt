package com.example.mediaparktechtask.presentation.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaparktechtask.databinding.ItemNewsCardBinding
import com.example.mediaparktechtask.databinding.ItemSearchHistoryFieldBinding
import com.example.mediaparktechtask.domain.contract.ImageDownloader
import com.example.mediaparktechtask.domain.entity.NewsCard
import kotlinx.coroutines.*
import javax.inject.Inject

class MultiAdapter @Inject constructor(
    private val imageDownloader: ImageDownloader
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemList = arrayListOf<RecyclerViewItem>()
    private var itemClickListener: ((Item) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Item.TYPE_NEWS -> NewsHolder(
                ItemNewsCardBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            Item.TYPE_LAST_SEARCHES -> LastSearchesHolder(
                ItemSearchHistoryFieldBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            else -> throw Exception("invalid menu item type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as MultiItemViewHolder) {
            bind(itemList[position].item)
        }
    }

    fun setItems(items: List<RecyclerViewItem>) {
        this.itemList.clear()
        this.itemList.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(itemClickListener: (Item) -> Unit) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = itemList.size
    override fun getItemViewType(position: Int): Int = itemList[position].itemType

    abstract inner class MultiItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Item)
        fun setOnClickListener(item: Item) {
            itemView.setOnClickListener {
                itemClickListener?.invoke(item)
            }
        }
    }

    inner class LastSearchesHolder(
        private val binding: ItemSearchHistoryFieldBinding
    ) : MultiItemViewHolder(binding.root) {
        override fun bind(item: Item) {
            with(item as Item.LastSearchesItem) {
                binding.searchHistoryText.text = item.title
                setOnClickListener(item)
            }
        }
    }

    inner class NewsHolder(private val binding: ItemNewsCardBinding) :
        MultiItemViewHolder(binding.root) {
        override fun bind(item: Item) {
            with(item as Item.NewsItem) {
                with(binding) {
                    newsCardItemTitle.text = item.news.title.toNewsTitle()
                    newsCardItemContent.text = item.news.content.toNewsDescription()
                    setOnClickListener(item)

                    GlobalScope.launch {
                        newsCardItemImage.setImage(binding, item.news)
                    }
                }
            }
        }

        private suspend fun ImageView.setImage(binding: ItemNewsCardBinding, newsCard: NewsCard) {
            if (newsCard.image == null) {
                var image: Bitmap
                withContext(Dispatchers.IO) {
                    image = imageDownloader.getBitmapFromLink(newsCard.imageLink)
                    newsCard.image = image
                    withContext(Dispatchers.Main) {
                        this@setImage.setImageBitmap(image)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    this@setImage.setImageBitmap(newsCard.image!!)
                }
            }
            withContext(Dispatchers.Main) {
                binding.newsLoadingAnimation.it.visibility = View.GONE
            }
        }
    }
}

private fun String.toNewsTitle(): String {
    return if (this.length >= 47) {
        this.substring(0, 37) + "..."
    } else this
}

private fun String.toNewsDescription(): String {
    return if (this.length >= 67) {
        this.substring(0, 47) + "..."
    } else this
}