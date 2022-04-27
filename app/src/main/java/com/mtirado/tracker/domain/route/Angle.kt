package com.mtirado.tracker.domain.route

import formatters.AngleFormatter
import kotlin.math.PI
import kotlin.math.abs

class Angle(val value: Double) {
    fun toRadians(): Double = value * PI / 180.0
    val absoluteValue: Angle get() = Angle(abs(this.value))

    override fun toString(): String {
        return AngleFormatter().format(this)
    }

    companion object {
        fun fromRadians(radians: Double): Angle {
            return Angle(radians * 180.0 / PI)
        }
    }
}
