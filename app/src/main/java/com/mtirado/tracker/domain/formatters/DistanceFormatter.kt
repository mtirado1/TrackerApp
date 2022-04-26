package com.mtirado.tracker.domain.formatters

import com.mtirado.tracker.domain.route.Constants.Companion.METERS_IN_A_KILOMETER
import com.mtirado.tracker.domain.route.Constants.Companion.METERS_IN_A_MILE
import com.mtirado.tracker.domain.route.Constants.Companion.SECONDS_IN_AN_HOUR

class UnitsFormatter {
    fun format(value: Double, units: Unit): String {
        return "%.2f".format(units.from(value))
    }
}

interface Unit {
    fun from(value: Double): Double
}

enum class DistanceUnits: Unit {
    METERS, MILES, KILOMETERS;

    override fun toString(): String {
        return when (this) {
            MILES -> "mi"
            METERS -> "m"
            KILOMETERS -> "km"
        }
    }

    override fun from(value: Double): Double {
        return when (this) {
            METERS -> value
            KILOMETERS -> value / METERS_IN_A_KILOMETER
            MILES -> value / METERS_IN_A_MILE
        }
    }
}

enum class SpeedUnits: Unit {
    METERS_PER_SECOND, KILOMETERS_PER_HOUR;

    override fun toString(): String {
        return when (this) {
            METERS_PER_SECOND -> "m/s"
            KILOMETERS_PER_HOUR -> "km/h"
        }
    }

    override fun from(value: Double): Double {
        return when(this) {
            METERS_PER_SECOND -> value
            KILOMETERS_PER_HOUR -> value * METERS_IN_A_KILOMETER / SECONDS_IN_AN_HOUR
        }
    }
}