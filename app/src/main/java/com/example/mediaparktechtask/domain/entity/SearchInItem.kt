package com.example.mediaparktechtask.domain.entity

import com.example.mediaparktechtask.data.containers.SearchOptions

data class SearchInItem(
    var item: SearchOptions,
    var isSelected: Boolean = false
)