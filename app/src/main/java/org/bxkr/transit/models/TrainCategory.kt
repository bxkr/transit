package org.bxkr.transit.models

data class TrainCategory(
    val latinName: String,
    val name: String,
    val options: List<Option>,
    val tariffPlans: List<String>,
    val trainCategoryId: Int,
    val trainId: Int
)