package com.example.mediaparktechtask.presentation

object ClickEventHelper {
    lateinit var onBackButtonClick: () -> Unit
    lateinit var onClearButtonClick: () -> Unit
    lateinit var setCountOfFilters: (count: Int) -> Unit
}

object ViewEventHelper {
    lateinit var hideFilterPageViews: () -> Unit
    lateinit var showSearchBar: () -> Unit
    lateinit var hideSearchBar: () -> Unit
}