package org.bxkr.transit

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import org.bxkr.transit.databinding.ItemStationBinding
import org.bxkr.transit.models.Stop
import org.bxkr.transit.models.TripSchedule
import java.text.SimpleDateFormat

class StationAdapter(
    private val context: MainActivity, private val schedule: TripSchedule,
    departureStationId: Long, arrivalStationId: Long
) : RecyclerView.Adapter<StationAdapter.ItemStationViewHolder>() {

    private val departureStationIndex =
        schedule.stops.map { it.station.expressId }.indexOf(departureStationId)
    private val arrivalStationIndex =
        schedule.stops.map { it.station.expressId }.indexOf(arrivalStationId)

    class ItemStationViewHolder(
        private val context: MainActivity,
        private val binding: ItemStationBinding,
        private val departureStationIndex: Int,
        private val arrivalStationIndex: Int
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val parseFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", context.resources.configuration.locales[0])
        private val formatFormat =
            SimpleDateFormat("HH:mm", context.resources.configuration.locales[0])

        fun bind(stop: Stop, index: Int) {
            binding.stationName.text = stop.station.name
            binding.stopTime.text = parseFormat.parse(stop.departureTime)
                ?.let { formatFormat.format(it) }
            if (stop.skip) {
                binding.stopTime.visibility = View.INVISIBLE
            } else {
                binding.stopTime.visibility = View.VISIBLE
            }
            if (stop.skip && index in departureStationIndex until arrivalStationIndex + 1) {
                binding.borderView.background = AppCompatResources.getDrawable(context, R.drawable.stop_borders)
            } else {
                binding.borderView.background = null
            }
            if (stop.skip || index !in departureStationIndex until arrivalStationIndex + 1) {
                binding.stationName.alpha = .5f
                binding.stopTime.alpha = .5f
                binding.root.setCardBackgroundColor(Color.TRANSPARENT)
            } else {
                binding.stopTime.alpha = 1f
                binding.stationName.alpha = 1f
                val attr = TypedValue()
                context.theme.resolveAttribute(
                    com.google.android.material.R.attr.colorSurfaceVariant,
                    attr,
                    true
                )
                binding.root.setCardBackgroundColor(attr.data)
            }
            if (stop.station.hasWicket) {
                binding.stationName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_wicket,
                    0,
                    0,
                    0
                )
            } else {
                binding.stationName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
            binding.root.shapeAppearanceModel =
                binding.root.shapeAppearanceModel.toBuilder().setAllCornerSizes(0f).build()
            binding.imageView.visibility = View.INVISIBLE
            if (index == departureStationIndex) {
                binding.imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_round_outlined_flag_24
                    )
                )
                binding.imageView.visibility = View.VISIBLE
                binding.root.shapeAppearanceModel =
                    binding.root.shapeAppearanceModel.toBuilder().apply {
                        setTopLeftCornerSize(12.dp)
                        setTopRightCornerSize(12.dp)
                    }.build()
            }
            if (index == arrivalStationIndex) {
                binding.imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_round_flag_circle_24
                    )
                )
                binding.imageView.visibility = View.VISIBLE
                binding.root.shapeAppearanceModel =
                    binding.root.shapeAppearanceModel.toBuilder().apply {
                        setBottomLeftCornerSize(12.dp)
                        setBottomRightCornerSize(12.dp)
                    }.build()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemStationViewHolder {
        val binding = ItemStationBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemStationViewHolder(context, binding, departureStationIndex, arrivalStationIndex)
    }

    override fun onBindViewHolder(holder: ItemStationViewHolder, position: Int) {
        val station = schedule.stops[position]
        holder.bind(station, position)
    }

    override fun getItemCount(): Int = schedule.stops.size
}