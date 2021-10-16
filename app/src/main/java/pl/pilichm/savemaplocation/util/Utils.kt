package pl.pilichm.savemaplocation.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.pilichm.savemaplocation.models.Location
import pl.pilichm.savemaplocation.util.Constants.Companion.EMPTY_LOCATION_DATA


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

        fun getEmptyLocationString(): String {
            val location = Location(EMPTY_LOCATION_DATA, EMPTY_LOCATION_DATA, EMPTY_LOCATION_DATA)
            return Json.encodeToString(location)
        }
    }
}