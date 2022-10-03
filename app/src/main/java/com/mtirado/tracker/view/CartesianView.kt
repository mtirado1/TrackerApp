package com.mtirado.tracker.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.mtirado.tracker.R
import com.mtirado.tracker.domain.route.Angle
import com.mtirado.tracker.domain.route.compareTo
import com.mtirado.tracker.domain.route.Route

class CartesianView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {
    private var route: Route? = null
    private var plotter = Plotter()

    fun setRoute(route: Route) {
        this.route = route
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawGrid(canvas, 4, 4)

        route?.let {
            drawProfile(it, canvas)
        }
    }

    private fun drawGrid(canvas: Canvas, verticalDivisions: Int, horizontalDivisions: Int) {
        plotter.draw(canvas) {
            stroke(R.color.gray, 1)
            for(i in 1 until verticalDivisions) {
                val y = height * i.toDouble() / verticalDivisions.toDouble()
                line(0, y, width, y)
            }

            for(i in 1 until horizontalDivisions) {
                val x = width * i.toDouble() / verticalDivisions.toDouble()
                line(x, 0, x, height)
            }
        }
    }

    private fun drawProfile(route: Route, canvas: Canvas) {
        val bounds = getBounds(route)
        var distance: Double = 0.0
        plotter.draw(canvas) {
            for(i in 0 until route.path.points.size - 1) {
                stroke(R.attr.colorSecondary, 1)
                val dx = route.path.points[i].distanceTo(route.path.points[i+1])
                val x1 = distance / bounds.length
                val x2 = (distance + dx) / bounds.length

                val y1 = 1.0  - bound(route.path.points[i].altitude, bounds.minAltitude, bounds.maxAltitude, 0, 1)
                val y2 = 1.0 - bound(route.path.points[i + 1].altitude, bounds.minAltitude, bounds.maxAltitude, 0, 1)

                line(x1 * width, y1 * height, x2 * width, y2 * height)
                distance += dx
            }
        }
    }

    private fun bound(x: Number, fromMin: Number, fromMax: Number, toMin: Number, toMax: Number): Double {
        return (x.toDouble() - fromMin.toDouble()) * (toMax.toDouble() - toMin.toDouble()) / (fromMax.toDouble() - fromMin.toDouble()) + toMin.toDouble()
    }

    private fun getBounds(route: Route): CartesianBounds {
        var minLongitude = Angle(0.0)
        var maxLongitude = Angle(0.0)
        var minLatitude = Angle(0.0)
        var maxLatitude = Angle(0.0)
        var minAltitude: Double = 0.0
        var maxAltitude: Double = 0.0

        route.path.points.forEach { point ->
            when {
                point.longitude < minLongitude -> minLongitude = point.longitude
                point.longitude > maxLongitude -> maxLongitude = point.longitude
            }
            when {
                point.latitude < minLatitude -> minLatitude = point.latitude
                point.latitude > maxLatitude -> maxLatitude = point.latitude
            }
            when  {
                point.altitude < minAltitude -> minAltitude = point.altitude
                point.altitude > maxAltitude -> maxAltitude = point.altitude
            }
        }
        return CartesianBounds(
            route.path.distance,
            minLongitude,
            maxLongitude,
            minLatitude,
            maxLatitude,
            minAltitude,
            maxAltitude
        )
    }
}

data class CartesianBounds(
    val length: Double,
    val minLongitude: Angle,
    val maxLongitude: Angle,
    val minLatitude: Angle,
    val maxLatitude: Angle,
    val minAltitude: Double,
    val maxAltitude: Double
)
