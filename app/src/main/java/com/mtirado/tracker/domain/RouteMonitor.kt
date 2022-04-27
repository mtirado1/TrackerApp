package com.mtirado.tracker.domain

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.mtirado.tracker.domain.route.*
import com.mtirado.tracker.service.LocationService
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import java.util.*

class RouteMonitor(val locationClient: FusedLocationProviderClient, val activity: Activity) : Monitor<Route> {
    private var route: Route? = null
    private var running = false
    private var interval: Int = 1
    private var currentJob: Job? = null
    private var cancellationTokenSource = CancellationTokenSource()

    override val routeObservable: PublishSubject<Route> = PublishSubject.create()
    override val isRunning: Boolean get() = running

    override fun start(value: Route, intervalInSeconds: Int) {
        this.interval = intervalInSeconds
        if (!running) {
            running = true
            route = value
            logData()
            startService()
        }
    }

    private fun startService() {
        val context = activity.applicationContext
        val intent = Intent(context, LocationService::class.java)
        context.startService(intent)
    }

    private fun stopService() {
        val context = activity.applicationContext
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
    }

    override fun resume() {
        running = true
        logData()
    }

    override fun stop()  {
        running = false
        route?.let { route ->
            routeObservable.onNext(route)
        }
    }

    override fun end(): Route? {
        running = false
        stopService()
        return route
    }

    private fun makeJob(intervalInSeconds: Int): Job {
        return MainScope().launch {
            while(running) {
                route?.let { route ->
                    getPosition { coordinates ->
                        val last = route.lastPosition
                        if (last == null) {
                            route.path.add(coordinates)
                            routeObservable.onNext(route)
                        }
                        else {
                            if (last.timestamp != coordinates.timestamp) {
                                route.path.add(coordinates)
                                routeObservable.onNext(route)
                            }
                        }
                    }
                }
                delay((intervalInSeconds * 1000).toLong())
            }
        }
    }

    private fun logData() {
        currentJob = makeJob(this.interval)
    }

    private fun getPosition(callback: (Coordinates) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                locationClient.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                locationClient.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            activity.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE)
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location: Location? ->
            location?.let {
                println("LOG! ${Date(location.time)}")
                callback(
                    Coordinates(
                        Angle(location.latitude),
                        Angle(location.longitude),
                        location.time,
                        location.altitude
                    )
                )
            }
        }
    }

    companion object {
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
        private const val PRIORITY_BALANCED_POWER_ACCURACY = 102
        private const val PRIORITY_HIGH_ACCURACY = 100
    }
}