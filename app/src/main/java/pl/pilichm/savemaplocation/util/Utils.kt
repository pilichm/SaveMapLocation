package pl.pilichm.savemaplocation.util

import pl.pilichm.savemaplocation.models.Location


class Utils {
    companion object {
        fun getMockLocationData(): ArrayList<Location> {
            val locations = ArrayList<Location>()

            locations.add(Location(
                "Warsaw",
                "Longitude: 11",
                "Latitude: 12"))
            locations.add(Location(
                "Berlin",
                "Longitude: 13",
                "Latitude: 14"))
            locations.add(Location(
                "Paris",
                "Longitude: 15",
                "Latitude: 16"))

            return locations
        }
    }
}