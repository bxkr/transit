package org.bxkr.transit.models

data class Stop(
    val arrivalTime: String,
    val departureTime: String,
    val deviation: Int,
    val skip: Boolean,
    val station: Station
)