package pl.pilichm.savemaplocation.util

import pl.pilichm.savemaplocation.models.Location


class Utils {
    companion object {
        fun getMockLocationData(): ArrayList<Location> {
            val locations = ArrayList<Location>()

            locations.add(Location(
                "Warsaw",
                "52.22",
                "21.01"))
            locations.add(Location(
                "Berlin",
                "52.52",
                "13.41"))
            locations.add(Location(
                "Paris",
                "48.85",
                "2.34"))

            return locations
        }
    }
}