package de.turksat46.opendashboard.common

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val speed: Float // in m/s
)