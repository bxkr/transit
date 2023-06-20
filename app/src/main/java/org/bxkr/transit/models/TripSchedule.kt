package org.bxkr.transit.models

data class TripSchedule(
    val finishStation: Station,
    val finishTime: String,
    val motionMode: String,
    val startStation: Station,
    val startTime: String,
    val stops: List<Stop>,
    val trainCategoryId: Int,
    val trainNumber: String,
    val tripId: Int
)