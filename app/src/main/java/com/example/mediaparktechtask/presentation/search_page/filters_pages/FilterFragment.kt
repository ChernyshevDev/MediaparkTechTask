package com.example.mediaparktechtask.presentation.search_page.filters_pages

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.databinding.FragmentFilterBinding
import com.example.mediaparktechtask.data.containers.Date
import com.example.mediaparktechtask.data.containers.SearchDataContainer
import com.example.mediaparktechtask.data.containers.SearchOptions
import com.example.mediaparktechtask.data.containers.calculateFilters
import com.example.mediaparktechtask.presentation.ClickEventHelper
import com.example.mediaparktechtask.presentation.ViewEventHelper

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    private lateinit var datePickerDialog: DatePickerDialog

    private var dateFrom: Date? = SearchDataContainer.data.value?.filter?.dateFrom
    private var dateTo: Date? = SearchDataContainer.data.value?.filter?.dateTo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupDatePickerDialog()
        setupClickListeners()
        overrideDeviceBackButton()
    }

    private fun setupClickListeners() {
        setDatePickersOnClick()
        setSearchInButtonClick()
        setBackButtonClick()
        setApplyFilterButtonClick()
        setClearButtonClick()
    }

    private fun overrideDeviceBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goToSearchPage()
                }
            })
    }

    private fun setupViews() {
        with(binding) {
            filterPageSearchInCurrentPreferences.text =
                SearchDataContainer.data.value?.filter?.searchIn?.toViewString()

            if (dateFrom != null) {
                filterPageDateFrom.setDate(dateFrom!!)
            } else {
                filterPageDateFrom.setDateHint()
            }

            if (dateTo != null) {
                filterPageDateTo.setDate(dateTo!!)
            } else {
                filterPageDateTo.setDateHint()
            }
        }
    }

    private fun TextView.setDateHint() {
        this.setTextAppearance(R.style.hint_text)
        this.text = getString(R.string.date_hint)
    }

    private fun TextView.setDate(date: Date) {
        this.text = date.toViewData()
        this.setTextAppearance(R.style.bottom_sheet_item_title_text)
    }

    private fun Date.toViewData(): String = "${year}-${month}-${day}"

    private fun List<SearchOptions>.toViewString(): String {
        if (this.size > 2) return getString(R.string.all)
        var string = ""
        for (option in this) {
            string += option.toTitle() + ", "
        }
        return string.substring(0, string.length - 2)
    }

    private fun SearchOptions.toTitle(): String {
        return when (this) {
            is SearchOptions.SearchInDescription -> requireContext().getString(R.string.description)
            is SearchOptions.SearchInTitle -> requireContext().getString(R.string.title)
            else -> requireContext().getString(R.string.content)
        }
    }

    private fun setClearButtonClick() {
        ClickEventHelper.onClearButtonClick = {
            SearchDataContainer.data.postValue(
                SearchDataContainer.data.value!!.apply {
                    this.filter.dateFrom = null
                    this.filter.dateTo = null
                    filtersApplied = calculateFilters(this)
                }
            )

            dateFrom = null
            dateTo = null
            setupViews()
        }
    }

    private fun setApplyFilterButtonClick() {
        binding.applyFilterButton.setOnClickListener {
            SearchDataContainer.data.postValue(
                SearchDataContainer.data.value!!.apply {
                    this.filter.dateFrom = dateFrom
                    this.filter.dateTo = dateTo
                    filtersApplied = calculateFilters(this)
                }
            )
            goToSearchPage()
        }
    }

    private fun setBackButtonClick() {
        ClickEventHelper.onBackButtonClick = {
            goToSearchPage()
        }
    }

    private fun setSearchInButtonClick() {
        binding.filterPageSearchInButton.setOnClickListener {
            findNavController().navigate(R.id.action_filterFragment_to_searchInFragment)
        }
    }

    private fun setDatePickersOnClick() {
        with(binding) {
            filterPageDateFrom.setOnClickListener {
                datePickerDialog
                    .setOnDateSetListener { _, year, month, dayOfMonth ->
                        dateFrom = Date(
                            day = dayOfMonth,
                            month = month,
                            year = year
                        )
                        filterPageDateFrom.setDate(dateFrom!!)
                    }
                datePickerDialog.show()
            }

            filterPageDateTo.setOnClickListener {
                datePickerDialog
                    .setOnDateSetListener { _, year, month, dayOfMonth ->
                        dateTo = Date(
                            day = dayOfMonth,
                            month = month,
                            year = year
                        )
                        filterPageDateTo.setDate(dateTo!!)
                    }
                datePickerDialog.show()
            }
        }
    }

    private fun setupDatePickerDialog() {
        datePickerDialog = DatePickerDialog(requireContext())
    }

    private fun goToSearchPage() {
        ViewEventHelper.hideFilterPageViews()
        ViewEventHelper.showSearchBar()
        findNavController().navigate(R.id.action_filter_fragment_to_search_page)
    }
}