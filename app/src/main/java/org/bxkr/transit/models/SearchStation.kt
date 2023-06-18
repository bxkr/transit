package org.bxkr.transit.models

data class SearchStation(
    val directions: List<Direction>,
    val hasWicket: Boolean,
    val id: Long,
    val latinName: String,
    val name: String
)
