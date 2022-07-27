package com.mtirado.tracker.domain

sealed class RouteMonitorState {
    object RUNNING: RouteMonitorState()
    object PAUSED: RouteMonitorState()
    object ENDED: RouteMonitorState()

    val isRunning: Boolean get() = this == RUNNING
}
