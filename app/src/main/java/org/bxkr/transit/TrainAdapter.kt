package org.bxkr.transit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import org.bxkr.transit.databinding.ItemTrainBinding
import org.bxkr.transit.fragments.TrainFragment
import org.bxkr.transit.models.DateTravel
import org.bxkr.transit.models.TrainCategory
import java.text.SimpleDateFormat

class TrainAdapter(
    private val context: MainActivity,
    private val trains: List<DateTravel>,
    private val categories: List<TrainCategory>
) : RecyclerView.Adapter<TrainAdapter.ItemTrainViewHolder>() {
    class ItemTrainViewHolder(
        private val binding: ItemTrainBinding,
        private val categories: List<TrainCategory>,
        private val activity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {
        private val parseFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", activity.resources.configuration.locales[0])
        private val formatFormat =
            SimpleDateFormat("HH:mm", activity.resources.configuration.locales[0])
        private val dateFormatFormat =
            SimpleDateFormat("yyyy-MM-dd", activity.resources.configuration.locales[0])

        fun bind(train: DateTravel) {
            with(binding) {
                trainType.text =
                    categories.first { it.trainCategoryId == train.trainCategoryId }.name
                trainNumber.text =
                    activity.getString(R.string.train_number_template, train.trainNumber)
                trainRouteText.text = activity.getString(
                    R.string.train_route_template, train.startStationName, train.finishStationName
                )
                trainPrice.text =
                    activity.getString(R.string.train_price_template, train.cost.toString())
                trainDepartureTime.text = activity.getString(R.string.train_dep_time_template,
                    parseFormat.parse(train.departureTime)?.let { formatFormat.format(it) })
                trainArrivalTime.text = activity.getString(R.string.train_arr_time_template,
                    parseFormat.parse(train.arrivalTime)?.let { formatFormat.format(it) })
                root.setOnClickListener {
                    activity.supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        add(
                            android.R.id.content, TrainFragment(
                                activity,
                                train,
                                dateFormatFormat.format(parseFormat.parse(train.startTime)!!)
                            )
                        )
                        addToBackStack(null)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTrainViewHolder {
        val binding = ItemTrainBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemTrainViewHolder(binding, categories, context)
    }

    override fun onBindViewHolder(holder: ItemTrainViewHolder, position: Int) {
        val train = trains[position]
        holder.bind(train)
    }

    override fun getItemCount(): Int = trains.size
}