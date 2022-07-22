package com.mtirado.tracker.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.mtirado.tracker.domain.route.Angle
import com.mtirado.tracker.domain.route.compareTo
import com.mtirado.tracker.domain.route.Route

class CartesianView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {
    private var route: Route? = null

    fun setRoute(route: Route) {
        this.route = route
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun getBounds(route: Route): CartesianBounds {
        var minLongitude = Angle(0.0)
        var maxLongitude = Angle(0.0)
        var minLatitude = Angle(0.0)
        var maxLatitude = Angle(0.0)
        route.path.points.forEach { point ->
            when {
                point.longitude < minLongitude -> minLongitude = point.longitude
                point.longitude > maxLongitude -> maxLongitude = point.longitude
            }
            when {
                point.latitude < minLatitude -> minLatitude = point.latitude
                point.latitude > maxLatitude -> maxLatitude = point.latitude
            }
        }
        return CartesianBounds(
            minLongitude,
            maxLongitude,
            minLatitude,
            maxLatitude
        )
    }
}

data class AltitudeBounds(
    val minAltitude: Double,
    val maxAltitude: Double
)

data class CartesianBounds(
    val minLongitude: Angle,
    val maxLongitude: Angle,
    val minLatitude: Angle,
    val maxLatitude: Angle
)
