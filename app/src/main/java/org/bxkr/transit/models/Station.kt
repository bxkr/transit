package org.bxkr.transit.models

data class Station(
    val direction: String,
    val expressId: Long,
    val hasExit: Boolean,
    val hasWicket: Boolean,
    val name: String,
    val stationAliasIds: Any
)