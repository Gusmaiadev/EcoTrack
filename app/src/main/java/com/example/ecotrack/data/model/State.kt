package com.example.ecotrack.data.model

data class State(
    val id: Long,
    val name: String,
    val abbreviation: String,
    val price_kwh: Double,
    val links: List<Any>
)