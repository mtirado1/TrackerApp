package com.mtirado.tracker.domain.route

class Path(val points: MutableList<Coordinates> = mutableListOf()) {
    val distance: Double get() = totalDistance()
    val duration: Long get() = totalTime()
    val size: Int get() = points.size
    val speed: Double get() = distance / duration
    val startTime: Long get() = points.first().timestamp
    val endTime: Long get() = points.last().timestamp

    val instantSpeed: Double get() {
        if (points.size < 2) return 0.0
        val last = points.takeLast(2)
        val dt = last[1].timestamp - last[0].timestamp
        return last[0].distanceTo(last[1]) / dt
    }

    fun add(point: Coordinates) {
        points.add(point)
    }

    private fun totalDistance(): Double {
        if (points.size < 2) return 0.0
        return reducePoints { a, b -> a.distanceTo(b) }.sum()
    }

    private fun totalTime(): Long {
        if(points.size < 2) return 0
        return endTime - startTime
    }

    fun maxAltitude(): Double? {
        return points.map { it.altitude }.maxOrNull()
    }

    fun minAltitude(): Double? {
        return points.map { it.altitude }.minOrNull()
    }

    fun altitudeGain(): Double {
        return points.last().altitude - points.first().altitude
    }

    fun maxSpeed(): Double? {
        return reducePoints { start, end -> Double
            start.distanceTo(end) / (end.timestamp - start.timestamp)
        }.maxOrNull()
    }

    private fun <T> reducePoints(function: (Coordinates, Coordinates) -> T): List<T> {
        var reduced: MutableList<T> = mutableListOf()
        for(i in 0 until points.size - 1) {
            reduced.add(function(points[i], points[i + 1]))
        }
        return reduced
    }
}