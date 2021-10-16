package pl.pilichm.savemaplocation.models

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val locationName: String = "",
    val longitude: String = "",
    val latitude: String = ""
    )
