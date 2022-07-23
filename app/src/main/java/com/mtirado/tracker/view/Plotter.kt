package com.mtirado.tracker.view

import android.graphics.Canvas
import android.graphics.Paint

// A simple wrapper DSL class for Canvas

fun Color(r: Double, g: Double, b: Double, a: Double = 1.0): Int {
    val alpha = (a * 255).toInt()
    val red = (r * 255.0).toInt()
    val green = (g * 255.0).toInt()
    val blue = (b * 255.0).toInt()

    return Color(red, green, blue, alpha)
}

fun Color(r: Int, g: Int, b: Int, a: Int = 255): Int {
    val alpha = (a and 0xff) shl 24
    val red = (r and 0xff) shl 16
    val green = (g and 0xff) shl 8
    val blue = (b and 0xff)

    return alpha or red or green or blue
}

class Plotter {
    var paint: Paint = Paint()
    private var canvas: Canvas = Canvas()
    val width get() = canvas.width
    val height get() = canvas.height

    fun draw(canvas: Canvas, block: Plotter.() -> Unit) {
        this.canvas = canvas
        this.block()
    }

    fun fill(color: Int) {
        this.paint.apply {
            this.color = color
            style = Paint.Style.FILL
        }
    }

    fun stroke(color: Int, width: Number) {
        this.paint.apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = width.toFloat()
        }
    }

    fun fillStroke(color: Int, width: Number) {
        this.paint.apply {
            this.color = color
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = width.toFloat()
        }
    }

    fun line(x1: Number, y1: Number, x2: Number, y2: Number) {
        canvas.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), paint)
    }

    fun point(x: Number, y: Number, radius: Number) {
        canvas.drawCircle(x.toFloat(), y.toFloat(), radius.toFloat(), paint)
    }

    fun <T> forEach(list: List<T>, action: Plotter.(T, Int) -> Unit) {
        list.forEachIndexed { index, obj -> this.action(obj, index) }
    }
}
