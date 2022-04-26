package formatters

import com.mtirado.tracker.domain.route.Angle
import com.mtirado.tracker.domain.route.Coordinates
import kotlin.math.abs

class CoordinatesFormatter {
    fun format(coordinates: Coordinates): String {
        val latitudeDirection = if(coordinates.latitude.value > 0) "N" else "S"
        val longitudeDirection = if(coordinates.longitude.value > 0) "E" else "W"

        val latitude = Angle(abs(coordinates.latitude.value))
        val longitude = Angle(abs(coordinates.longitude.value))

        val formatter = AngleFormatter()

        return "${formatter.format(latitude)}${latitudeDirection} ${formatter.format(longitude)}${longitudeDirection}"
    }
}