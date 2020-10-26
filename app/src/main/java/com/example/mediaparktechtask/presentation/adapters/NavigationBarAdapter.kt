package com.example.mediaparktechtask.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.databinding.ItemBottomNavigationBinding
import com.example.mediaparktechtask.domain.entity.NavigationBarItem
import javax.inject.Inject

class NavigationBarAdapter @Inject constructor() :
    RecyclerView.Adapter<NavigationBarAdapter.ItemViewHolder>() {

    var items: List<NavigationBarItem> = listOf()
    lateinit var onItemClick: (item: NavigationBarItem) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemBottomNavigationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(private val binding: ItemBottomNavigationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(currentItem: NavigationBarItem) {
            with(binding) {

                navigationItemHeader.text = currentItem.title
                navigationItemImage.setImageResource(currentItem.imageResourceId)
                navigationItemImage.setHighlighted(currentItem.isSelected)

                navigationItem.setOnClickListener {
                    unselectAllItems()
                    currentItem.isSelected = true

                    onItemClick(currentItem)
                    this@NavigationBarAdapter.notifyDataSetChanged()
                }
            }
        }

        private fun unselectAllItems() {
            for (item in items) {
                item.isSelected = false
            }
        }

        private fun ImageView.setHighlighted(isSelected: Boolean) {
            if (isSelected) {
                DrawableCompat.setTint(
                    this.drawable,
                    ContextCompat.getColor(context, R.color.colorNavigationItemSelected)
                )
            } else {
                DrawableCompat.setTint(
                    this.drawable,
                    ContextCompat.getColor(context, R.color.colorNavigationItemUnselected)
                )
            }
        }
    }
}