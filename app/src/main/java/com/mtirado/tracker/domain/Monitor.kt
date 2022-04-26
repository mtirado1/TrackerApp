package com.mtirado.tracker.domain
import io.reactivex.subjects.PublishSubject

interface Monitor<T> {
    fun start(value: T, intervalInSeconds: Int)
    fun resume()
    fun stop()
    fun end(): T?

    val isRunning: Boolean
    val routeObservable: PublishSubject<T>
}
