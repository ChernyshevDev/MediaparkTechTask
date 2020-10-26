package com.example.mediaparktechtask.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.databinding.ItemSortByUploadDateBinding
import com.example.mediaparktechtask.domain.entity.SearchInItem
import com.example.mediaparktechtask.data.containers.SearchOptions
import javax.inject.Inject

class SearchInAdapter @Inject constructor(
    private val context: Context
) : RecyclerView.Adapter<SearchInAdapter.ItemViewHolder>() {

    private var itemList = mutableListOf<SearchInItem>()
    private lateinit var onClick: ((item: SearchInItem) -> Unit)

    fun setItems(items: List<SearchInItem>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    fun deselectAllButSearchInTitle() {
        for (item in itemList) {
            item.isSelected = item.item is SearchOptions.SearchInTitle
        }
        notifyDataSetChanged()
    }

    fun setOnClick(doing: (item: SearchInItem) -> Unit) {
        onClick = doing
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemSortByUploadDateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    inner class ItemViewHolder(private val binding: ItemSortByUploadDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(currentItem: SearchInItem) {
            with(binding) {
                title.text = currentItem.item.toTitle()
                selector.setDrawable(currentItem.isSelected)

                this.root.setOnClickListener {
                    currentItem.isSelected = !currentItem.isSelected
                    selector.setDrawable(currentItem.isSelected)

                    onClick(currentItem)
                }
            }
        }
    }

    private fun ImageView.setDrawable(isSelected: Boolean) {
        if (isSelected) {
            setImageResource(R.drawable.ic_toggle_on)
        } else {
            setImageResource(R.drawable.ic_toggle_off)
        }
    }

    fun SearchOptions.toTitle(): String {
        return when (this) {
            is SearchOptions.SearchInDescription -> context.getString(R.string.description)
            is SearchOptions.SearchInTitle -> context.getString(R.string.title)
            else -> context.getString(R.string.content)
        }
    }
}