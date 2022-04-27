package formatters

import com.mtirado.tracker.domain.route.Angle
import com.mtirado.tracker.domain.route.Coordinates

class CoordinatesFormatter {
    fun format(coordinates: Coordinates): String {
        return "${formatLatitude(coordinates.latitude)} ${formatLongitude(coordinates.longitude)}"
    }

    fun formatLatitude(angle: Angle): String {
        val direction = if(angle.value > 0) "N" else "S"
        val latitude = angle.absoluteValue
        return "$latitude$direction"
    }

    fun formatLongitude(angle: Angle): String {
        val direction = if(angle.value > 0) "E" else "W"
        val longitude = angle.absoluteValue
        return "$longitude$direction"
    }
}