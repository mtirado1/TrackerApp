package com.mtirado.tracker.view

import android.graphics.Canvas
import android.graphics.Paint

// A simple wrapper DSL class for Canvas

class Plotter(private val canvas: Canvas) {
    var paint: Paint = Paint()

    fun fill(color: Int): Plotter {
        this.paint.apply {
            this.color = color
            this.style = Paint.Style.FILL
        }
        return this
    }

    fun stroke(color: Int, width: Number): Plotter {
        this.paint.apply {
            this.color = color
            this.style = Paint.Style.STROKE
            this.strokeWidth = width.toFloat()
        }
        return this
    }

    fun line(x1: Number, y1: Number, x2: Number, y2: Number): Plotter {
        canvas.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), paint)
        return this
    }

    fun point(x: Number, y: Number, radius: Number): Plotter {
        canvas.drawCircle(x.toFloat(), y.toFloat(), radius.toFloat(), paint)
        return this
    }

    fun <T> forEach(list: List<T>, action: Plotter.(Int, T) -> Unit): Plotter {
        list.forEachIndexed { index, obj -> this.action(index, obj) }
        return this
    }
}
