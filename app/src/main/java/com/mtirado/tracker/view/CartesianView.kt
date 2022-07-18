package com.mtirado.tracker.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
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
}
