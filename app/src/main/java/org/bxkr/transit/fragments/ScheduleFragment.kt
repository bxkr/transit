package org.bxkr.transit.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import org.bxkr.transit.BaseCallback
import org.bxkr.transit.MainActivity
import org.bxkr.transit.NetworkService
import org.bxkr.transit.R
import org.bxkr.transit.TrainAdapter
import org.bxkr.transit.databinding.FragmentScheduleBinding
import org.bxkr.transit.models.DateTravel
import org.bxkr.transit.models.SearchStation
import org.bxkr.transit.models.TrainCategory
import java.text.SimpleDateFormat
import java.util.Calendar

class ScheduleFragment : BaseFragment<FragmentScheduleBinding>(FragmentScheduleBinding::inflate) {
    private var verifiedDepartureStation = false
    private var verifiedArrivalStation = false
    private var latestVerifiedDepartureStation = String()
    private var depId = 0L
    private var latestVerifiedArrivalStation = String()
    private var arrId = 0L
    private var latestSuggestions = listOf<SearchStation>()
    private var latestSuggestionsStrings = listOf<String>()
    private var datePicked = String()
    private var doNotListen = false

    private enum class SearchField {
        Departure, Arrival
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onSearchChange = { text: String, field: SearchField ->
            if (latestSuggestionsStrings.contains(text)) {
                when (field) {
                    SearchField.Departure -> {
                        verifiedDepartureStation = true
                        latestVerifiedDepartureStation = text
                        depId = latestSuggestions.first { it.name == text }.id
                        if (verifiedArrivalStation) search()
                    }

                    SearchField.Arrival -> {
                        verifiedArrivalStation = true
                        latestVerifiedArrivalStation = text
                        arrId = latestSuggestions.first { it.name == text }.id
                        if (verifiedDepartureStation) search()
                    }
                }
            } else {
                when (field) {
                    SearchField.Departure -> verifiedDepartureStation = false
                    SearchField.Arrival -> verifiedArrivalStation = false
                }
            }
            val call = NetworkService.api().searchStation(text)
            call.enqueue(object : BaseCallback<List<SearchStation>>(
                parentContext = activity as MainActivity,
                bindingRoot = binding.root,
                function = {
                    latestSuggestionsStrings = it.body()!!.map { it1 -> it1.name }
                    latestSuggestions = it.body()!!
                    val searchField: AutoCompleteTextView = when (field) {
                        SearchField.Departure -> binding.departureStationSearch
                        SearchField.Arrival -> binding.arrivalStationSearch
                    }
                    searchField.setAdapter(
                        ArrayAdapter(
                            activity as MainActivity,
                            android.R.layout.select_dialog_item,
                            latestSuggestionsStrings
                        )
                    )
                }
            ) {})
        }
        binding.departureStationSearch.doOnTextChanged { text, _, _, _ ->
            if (!doNotListen) {
                onSearchChange(text.toString(), SearchField.Departure)
            }
        }
        binding.arrivalStationSearch.doOnTextChanged { text, _, _, _ ->
            if (!doNotListen) {
                onSearchChange(text.toString(), SearchField.Arrival)
            }
        }
        binding.departureStationSearch.setOnFocusChangeListener { view1, b ->
            if (!b) {
                (view1 as AutoCompleteTextView).setText(latestVerifiedDepartureStation)
            } else (view1 as AutoCompleteTextView).setText(String())
        }
        binding.arrivalStationSearch.setOnFocusChangeListener { view1, b ->
            if (!b) {
                (view1 as AutoCompleteTextView).setText(latestVerifiedArrivalStation)
            } else (view1 as AutoCompleteTextView).setText(String())
        }
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    getString(R.string.pick_date) -> showDatePickerDialog { search() }
                    else -> search()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.position == 2) {
                    tab.text = getString(R.string.pick_date)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // reselect
            }
        })
        binding.swapButton.setOnClickListener {
            doNotListen = true
            binding.arrivalStationSearch.setText(latestVerifiedDepartureStation)
            binding.departureStationSearch.setText(latestVerifiedArrivalStation)
            depId = arrId.also { arrId = depId }
            latestVerifiedDepartureStation = latestVerifiedArrivalStation.also {
                latestVerifiedArrivalStation = latestVerifiedDepartureStation
            }
            search()
            doNotListen = false
        }
    }

    fun showDatePickerDialog(onResult: () -> Unit) {
        val newFragment = DatePickerFragment { year, month, day ->
            val format = SimpleDateFormat("dd MMM yyyy", resources.configuration.locales[0])
            val decodeFormat = SimpleDateFormat("dd-MM-yyyy", resources.configuration.locales[0])
            binding.tabLayout.getTabAt(2)?.text =
                decodeFormat.parse("$day-${month + 1}-$year")?.let { format.format(it) }
            datePicked = "$year-${month + 1}-$day"
            onResult()
        }
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    fun search() {
        binding.travels.visibility = View.GONE
        val calendar = Calendar.getInstance()
        val date = when (binding.tabLayout.selectedTabPosition) {
            0 -> "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
                calendar.get(
                    Calendar.DAY_OF_MONTH
                )
            }"

            1 -> "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
                calendar.get(
                    Calendar.DAY_OF_MONTH
                ) + 1
            }"

            2 -> datePicked
            else -> "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
                calendar.get(
                    Calendar.DAY_OF_MONTH
                )
            }" // today
        }
        val categoryCall = NetworkService.api().trainCategory()
        categoryCall.enqueue(object : BaseCallback<List<TrainCategory>>(
            parentContext = activity as MainActivity,
            function = {
                val categories = it.body()!!
                val call = NetworkService.api().dateTravel(date, depId, arrId)
                call.enqueue(object : BaseCallback<List<DateTravel>>(
                    parentContext = activity as MainActivity,
                    function = { it1 ->
                        if (it1.body()!!.isNotEmpty()) {
                            binding.tabLayout.visibility = View.VISIBLE
                            binding.travels.visibility = View.VISIBLE
                            binding.travels.layoutManager = LinearLayoutManager(requireContext())
                            binding.travels.adapter =
                                TrainAdapter(activity as MainActivity, it1.body()!!, categories)
                        }
                    }
                ) {})
            }
        ) {})
    }
}