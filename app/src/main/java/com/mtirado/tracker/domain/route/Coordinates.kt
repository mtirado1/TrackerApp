package com.mtirado.tracker.domain.route

import com.mtirado.tracker.domain.route.Constants.Companion.EARTH_RADIUS
import formatters.CoordinatesFormatter
import kotlin.math.*

class Coordinates (
    val latitude: Angle,
    val longitude: Angle,
    val timestamp: Long,
    val altitude: Double = 0.0
) {
    fun distanceTo(other: Coordinates): Double {
        val latitude1 = this.latitude.toRadians()
        val latitude2 = other.latitude.toRadians()

        val deltaLatitude = latitude2 - latitude1
        val deltaLongitude = other.longitude.toRadians() - this.longitude.toRadians()

        val a = sin2(deltaLatitude / 2) + cos(latitude1) * cos(latitude2) * sin2(deltaLongitude / 2)
        return 2 * EARTH_RADIUS * atan2(sqrt(a), sqrt(1 - a))
    }

    private fun sin2(x: Double): Double {
        val sin = sin(x)
        return sin * sin
    }

    val latitudeString: String get() = CoordinatesFormatter().formatLatitude(this.latitude)
    val longitudeString: String get() = CoordinatesFormatter().formatLongitude(this.longitude)

    override fun toString(): String {
        return CoordinatesFormatter().format(this)
    }
}