package com.example.mediaparktechtask.domain.entity

data class NavigationBarItem(
    val title: String,
    val imageResourceId: Int,
    var isSelected: Boolean = false
)