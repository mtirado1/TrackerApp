package com.mtirado.tracker.domain
import io.reactivex.subjects.PublishSubject

interface Monitor<T> {
    fun start(intervalInSeconds: Int)
    fun resume()
    fun stop()
    fun end()

    val isRunning: Boolean
    val onValue: (T) -> Unit
}
