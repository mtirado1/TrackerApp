package formatters

import com.mtirado.tracker.domain.route.Angle
import kotlin.math.abs
import kotlin.math.floor

class AngleFormatter {
    fun format(angle: Angle): String {
        val value = abs(angle.value)

        val positive = angle.value > 0
        val degrees = floor(value).toInt()
        val minutes = floor(value * 60 - degrees * 60).toInt()
        val seconds = floor(value * 3600 - degrees * 3600 - minutes * 60).toInt()

        return "${if(positive) "" else "-"}${degrees}°${minutes}′${seconds}″"
    }
}