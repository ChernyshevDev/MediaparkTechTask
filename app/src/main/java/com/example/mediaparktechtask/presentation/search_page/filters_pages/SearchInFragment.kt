package com.example.mediaparktechtask.presentation.search_page.filters_pages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.databinding.FragmentSearchInBinding
import com.example.mediaparktechtask.data.containers.SearchDataContainer
import com.example.mediaparktechtask.data.containers.SearchOptions
import com.example.mediaparktechtask.presentation.adapters.SearchInAdapter
import com.example.mediaparktechtask.domain.entity.SearchInItem
import com.example.mediaparktechtask.presentation.ClickEventHelper
import com.example.mediaparktechtask.data.containers.calculateFilters
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SearchInFragment : Fragment() {

    @Inject
    lateinit var adapter: SearchInAdapter

    private lateinit var binding: FragmentSearchInBinding

    private var searchIn =
        SearchDataContainer.data.value!!.filter.searchIn

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchInBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindRecyclerView()
        setOnClickListeners()
    }

    private fun bindRecyclerView() {
        adapter.setItems(
            arrayListOf(
                SearchInItem(
                    item = SearchOptions.SearchInTitle,
                    isSelected = searchIn.contains(SearchOptions.SearchInTitle)
                ),
                SearchInItem(
                    item = SearchOptions.SearchInDescription,
                    isSelected = searchIn.contains(SearchOptions.SearchInDescription)
                ),
                SearchInItem(
                    item = SearchOptions.SearchInContent,
                    isSelected = searchIn.contains(SearchOptions.SearchInContent)
                )
            )
        )

        adapter.setOnClick { item ->
            if (item.isSelected) {
                searchIn.add(item.item)
            } else {
                searchIn.remove(item.item)
            }
        }

        binding.searchInTogglesRecycler.adapter = adapter
    }

    private fun setOnClickListeners() {
        setOnBackButtonClick()
        setOnApplyFilterButtonClick()
        setOnClearButtonClick()
    }

    private fun setOnApplyFilterButtonClick() {
        binding.applyFilterButton.setOnClickListener {
            if (searchIn.atLeastOneSelected()) {
                SearchDataContainer.data.postValue(
                    SearchDataContainer.data.value?.apply {
                        filter.searchIn = searchIn
                        filtersApplied = calculateFilters(this)
                    }
                )
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, getString(R.string.nothing_is_selected), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun List<SearchOptions>.atLeastOneSelected(): Boolean = this.isNotEmpty()

    private fun setOnBackButtonClick() {
        ClickEventHelper.onBackButtonClick = {
            findNavController().popBackStack()
        }
    }

    private fun setOnClearButtonClick() {
        /**
         * We should not give user opportunity to search in nowhere.
         * It makes no sense. So, when user clears "search in" options,
         * "search in title" will be selected by default.
         */
        ClickEventHelper.onClearButtonClick = {
            searchIn = arrayListOf(
                SearchOptions.SearchInTitle
            )

            adapter.deselectAllButSearchInTitle()

            SearchDataContainer.data.postValue(
                SearchDataContainer.data.value?.apply {
                    filter.searchIn = arrayListOf(
                        SearchOptions.SearchInTitle
                    )
                    filtersApplied = calculateFilters(this)
                }
            )
        }
    }
}