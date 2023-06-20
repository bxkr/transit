package org.bxkr.transit.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bxkr.transit.BaseCallback
import org.bxkr.transit.MainActivity
import org.bxkr.transit.NetworkService
import org.bxkr.transit.R
import org.bxkr.transit.StationAdapter
import org.bxkr.transit.models.DateTravel
import org.bxkr.transit.models.TripSchedule

class TrainFragment(
    private val mainActivity: MainActivity,
    private val train: DateTravel,
    private val date: String // format "yyyy-MM-dd"
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_train, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val call = NetworkService.api().tripSchedule(train.scheduleId, date)
        call.enqueue(object : BaseCallback<TripSchedule>(
            parentContext = mainActivity,
            function = {
                (view as RecyclerView).apply {
                    mainActivity.title = getString(R.string.train_fragment_title, train.trainNumber)
                    layoutManager = LinearLayoutManager(mainActivity)
                    adapter = StationAdapter(mainActivity, it.body()!!,
                        train.departureStationId, train.arrivalStationId)
                }
            }
        ) {})
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.title = getString(R.string.app_name)
    }
}