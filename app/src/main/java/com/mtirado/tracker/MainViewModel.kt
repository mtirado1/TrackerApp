package com.mtirado.tracker

import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.mtirado.tracker.domain.RouteMonitor
import com.mtirado.tracker.domain.RouteMonitorState
import com.mtirado.tracker.domain.route.Coordinates
import com.mtirado.tracker.domain.route.Route
import io.reactivex.subjects.BehaviorSubject

class MainViewModel: ViewModel() {
     private var route: Route? = null
     private lateinit var monitor: RouteMonitor
     var routeObservable: BehaviorSubject<Route> = BehaviorSubject.create()
     val monitorState: RouteMonitorState
          get() = when {
          route == null -> RouteMonitorState.ENDED
          monitor.isRunning -> RouteMonitorState.RUNNING
          else -> RouteMonitorState.PAUSED
     }

     fun createMonitor(activity: MainActivity) {
          monitor = RouteMonitor(LocationServices.getFusedLocationProviderClient(activity.applicationContext), activity) {
               monitorValueReceived(it)
          }
     }

     private fun monitorValueReceived(coordinates: Coordinates) {
          route?.let {
               it.path.add(coordinates)
               routeObservable.onNext(it)
          }
     }

     fun startMonitor(route: Route, logInterval: Int) {
          this.route = route
          monitor.start(logInterval)
     }

     fun stopMonitor() {
          monitor.stop()
          route?.let {
               routeObservable.onNext(it)
          }
     }

     fun resumeMonitor() {
          monitor.resume()
     }

     fun endMonitor(): Route? {
          monitor.end()
          val finalRoute = route
          route = null
          return finalRoute
     }
}