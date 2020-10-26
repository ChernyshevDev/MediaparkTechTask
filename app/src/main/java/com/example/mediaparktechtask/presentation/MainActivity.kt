package com.example.mediaparktechtask.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.data.containers.SearchDataContainer
import com.example.mediaparktechtask.data.containers.SortOptions
import com.example.mediaparktechtask.data.containers.updateState
import com.example.mediaparktechtask.databinding.MainScreenBinding
import com.example.mediaparktechtask.domain.entity.NavigationBarItem
import com.example.mediaparktechtask.presentation.adapters.NavigationBarAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigationBarAdapter: NavigationBarAdapter

    private lateinit var viewBinding: MainScreenBinding

    private lateinit var bottomSheet: ConstraintLayout
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        if (noInternet()) {
            setContentView(R.layout.no_internet_screen)
        } else {
            viewBinding = MainScreenBinding.inflate(layoutInflater)
            setContentView(viewBinding.root)

            initBottomSheet()
            setupNavigationBar()
            setupSearchBar()
            setupSortDialog()
        }
    }

    private fun setupNavigationBar() {
        setNavigationBarItems()
        setNavigationBarOnItemClicks()

        viewBinding.mainScreenNavigationBar.navigationBarRecycler.adapter =
            navigationBarAdapter
    }

    private fun setNavigationBarItems() {
        navigationBarAdapter.items = listOf(
            NavigationBarItem(
                title = getString(R.string.navigation_header_home),
                imageResourceId = R.drawable.ic_home,
                isSelected = true
            ),
            NavigationBarItem(
                title = getString(R.string.navigation_header_news),
                imageResourceId = R.drawable.ic_circles
            ),
            NavigationBarItem(
                title = getString(R.string.navigation_header_search),
                imageResourceId = R.drawable.ic_search
            ),
            NavigationBarItem(
                title = getString(R.string.navigation_header_profile),
                imageResourceId = R.drawable.ic_human
            ),
            NavigationBarItem(
                title = getString(R.string.navigation_header_more),
                imageResourceId = R.drawable.ic_more
            ),
        )
    }

    private fun setNavigationBarOnItemClicks() {
        navigationBarAdapter.onItemClick = { item ->
            if (item.title != getString(R.string.navigation_header_search))
                hideSearchBarView()
            when (item.title) {
                getString(R.string.navigation_header_home) -> onHomeClicked()
                getString(R.string.navigation_header_news) -> onNewsClicked()
                getString(R.string.navigation_header_search) -> onSearchClicked()
                getString(R.string.navigation_header_profile) -> onProfileClicked()
                getString(R.string.navigation_header_more) -> onMoreClicked()
                else -> {
                }
            }
        }
    }

    private fun setupSearchBar() {
        setupSortButton()
        setupFilterButton()
        setupSearchInputField()
        setupHelpers()
    }

    private fun setupHelpers() {
        with(ViewEventHelper) {
            hideFilterPageViews = { hideFilterPageAdditionalButtons() }
            showSearchBar = { showSearchBarView() }
            hideSearchBar = { hideSearchBarView() }
        }
        ClickEventHelper.setCountOfFilters = { count -> setFiltersCount(count) }

        with(viewBinding.mainScreenTopBarAdditionalButtons)
        {
            backButton.setOnClickListener { ClickEventHelper.onBackButtonClick() }
            clearButton.setOnClickListener { ClickEventHelper.onClearButtonClick() }
        }
    }

    private fun setupSortDialog() {
        setupBottomDialogBehaviour()
        setBottomDialogItemsIcons()
        setupBottomDialogOnItemClicks()
    }

    private fun setupSearchInputField() {
        with(viewBinding.mainScreenSearchBar.searchTextInputField) {
            this.setOnEditorActionListener { _, _, _ ->
                if (inputTextIsValid()) {
                    hideKeyboard(this)

                    val inputText = this.text.toString()
                    SearchDataContainer.updateState {
                        this.inputText = inputText
                        this.searchHistory.add(inputText)
                    }
                    return@setOnEditorActionListener true
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.invalid_input),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                false
            }
        }
    }

    private fun setupSortButton() {
        with(viewBinding.mainScreenSearchBar) {
            sortButton.isActivated = false
            sortButton.setOnClickListener {
                if (sortButton.isActivated) {
                    sortButton.setImageResource(R.drawable.ic_sort)
                    bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    sortButton.setImageResource(R.drawable.ic_sort_selected)
                    bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
                sortButton.isActivated = !sortButton.isActivated
            }
        }
    }

    private fun setupFilterButton() {
        viewBinding.mainScreenSearchBar.searchBarFilterButton.setOnClickListener {
            hideSearchBarView()
            viewBinding.mainScreenTopBar.elevation = 0f
            viewBinding.mainScreenTopBarAdditionalButtons.root.visibility = View.VISIBLE
            navController().navigate(R.id.action_search_page_to_filterFragment)
        }
    }

    private fun hideFilterPageAdditionalButtons() {
        viewBinding.mainScreenTopBar.elevation = 16f
        viewBinding.mainScreenTopBarAdditionalButtons.root.visibility = View.GONE
    }

    private fun setupBottomDialogOnItemClicks() {
        with(viewBinding.mainScreenSortByBottomDialog) {
            sortByRelevance.root.setOnClickListener {
                if (SearchDataContainer.data.value!!.sortBy != SortOptions.SortByRelevance) {
                    SearchDataContainer.updateState {
                        sortBy = SortOptions.SortByRelevance
                        if (searchHistory.isNotEmpty()) {
                            inputText = searchHistory.last()
                        }
                    }
                    setBottomDialogItemsIcons()
                }
            }

            sortByUploadDate.root.setOnClickListener {
                if (SearchDataContainer.data.value!!.sortBy != SortOptions.SortByUploadDate) {
                    SearchDataContainer.updateState {
                        sortBy = SortOptions.SortByUploadDate
                        if (searchHistory.isNotEmpty()) {
                            inputText = searchHistory.last()
                        }
                    }

                    setBottomDialogItemsIcons()
                }
            }
        }
    }

    private fun setBottomDialogItemsIcons() {
        with(viewBinding.mainScreenSortByBottomDialog) {
            if (SearchDataContainer.data.value!!.sortBy == SortOptions.SortByUploadDate) {
                sortByUploadDate.selector.setImageResource(R.drawable.ic_circle_selected)
                sortByRelevance.selector.setImageResource(R.drawable.ic_circle_unselected)
            } else {
                sortByRelevance.selector.setImageResource(R.drawable.ic_circle_selected)
                sortByUploadDate.selector.setImageResource(R.drawable.ic_circle_unselected)
            }
        }
    }

    private fun setupBottomDialogBehaviour() {
        bottomSheet.isClickable = true
        bottomSheetBehaviour.isHideable = true
        bottomSheetBehaviour.isDraggable = true
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehaviour.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewBinding.mainScreenSearchBar.sortButton.setImageResource(R.drawable.ic_sort)
                    bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                    viewBinding.mainScreenSearchBar.sortButton.isActivated = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun initBottomSheet() {
        bottomSheet = viewBinding.mainScreenSortByBottomDialog.root
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)
    }

    private fun onHomeClicked() {
        navigateToHomePage()
    }

    private fun onNewsClicked() {
        navigateToNewsPage()
    }

    private fun onSearchClicked() {
        showSearchBarView()
        navigateToSearchPage()
    }

    private fun onProfileClicked() {
        navigateToHomePage()
    }

    private fun onMoreClicked() {
        navigateToHomePage()
    }

    private fun navigateToNewsPage() {
        when (navController().currentDestination?.id) {
            R.id.home_page -> navController().navigate(R.id.action_home_page_to_newsPage)
            R.id.search_page -> navController().navigate(R.id.action_search_page_to_news_page)
            R.id.search_in_fragment -> {
                hideFilterPageAdditionalButtons()
                navController().navigate(R.id.action_search_in_fragment_to_news_page)
            }
            R.id.filter_fragment -> {
                hideFilterPageAdditionalButtons()
                navController().navigate(R.id.action_filter_fragment_to_news_page)
            }
            R.id.web_view -> navController().navigate(R.id.action_web_view_to_news_page)
            else -> {
            }
        }
    }

    private fun navigateToHomePage() {
        when (navController().currentDestination?.id) {
            R.id.news_page -> navController().navigate(R.id.action_newsPage_to_home_page)
            R.id.search_page -> navController().navigate(R.id.action_search_page_to_home_page)
            R.id.search_in_fragment -> {
                hideFilterPageAdditionalButtons()
                navController().navigate(R.id.action_search_in_fragment_to_home_page)
            }
            R.id.filter_fragment -> {
                hideFilterPageAdditionalButtons()
                navController().navigate(R.id.action_filter_fragment_to_home_page)
            }
            R.id.web_view -> navController().navigate(R.id.action_web_view_to_home_page)
            else -> {
            }
        }
    }

    private fun navigateToSearchPage() {
        when (navController().currentDestination?.id) {
            R.id.home_page -> navController().navigate(R.id.action_home_page_to_search_page)
            R.id.news_page -> navController().navigate(R.id.action_news_page_to_search_page)
            R.id.search_in_fragment -> {
                hideFilterPageAdditionalButtons()
                showSearchBarView()
                navController().navigate(R.id.action_search_in_fragment_to_search_page)
            }
            R.id.filter_fragment -> {
                hideFilterPageAdditionalButtons()
                showSearchBarView()
                navController().navigate(R.id.action_filter_fragment_to_search_page)
            }
            R.id.web_view -> {
                showSearchBarView()
                navController().navigate(R.id.action_web_view_to_search_page)
            }
            else -> {
            }
        }
    }

    private fun setFiltersCount(count: Int) {
        with(viewBinding.mainScreenSearchBar) {
            if (count == 0) {
                oval.visibility = View.GONE
                searchBarFiltersCount.visibility = View.GONE
            } else {
                oval.visibility = View.VISIBLE
                searchBarFiltersCount.visibility = View.VISIBLE
                searchBarFiltersCount.text = count.toString()
            }
        }
    }

    private fun showSearchBarView() {
        viewBinding.mainScreenSearchBar.root.visibility = View.VISIBLE
    }

    private fun hideSearchBarView() {
        viewBinding.mainScreenSearchBar.root.visibility = View.GONE
    }

    private fun inputTextIsValid(): Boolean {
        val text = viewBinding.mainScreenSearchBar.searchTextInputField.text.toString()
        if (text.length < 3 && text.length < 20) return false
        return true
    }

    private fun hideKeyboard(editText: EditText) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun navController(): NavController =
        findNavController(R.id.nav_host_fragment)

    private fun noInternet(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo?.isConnected != null)
            if (connectivityManager.activeNetworkInfo?.isConnected != false)
                return false
        return true
    }

}