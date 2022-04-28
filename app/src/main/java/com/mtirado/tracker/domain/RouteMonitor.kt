package com.mtirado.tracker.domain

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.mtirado.tracker.domain.route.*
import com.mtirado.tracker.service.LocationService
import io.reactivex.subjects.PublishSubject

class RouteMonitor(val locationClient: FusedLocationProviderClient, val activity: Activity) : Monitor<Route>, LocationCallback() {
    private var route: Route? = null
    private var running = false
    private var interval: Int = 1

    val last: Route? get() = if(running) route else null

    private lateinit var locationRequest: LocationRequest

    override val routeObservable: PublishSubject<Route> = PublishSubject.create()
    override val isRunning: Boolean get() = running

    override fun start(value: Route, intervalInSeconds: Int) {
        this.interval = intervalInSeconds
        if (!running) {
            running = true
            route = value

            locationRequest = LocationRequest.create().apply {
                interval = (intervalInSeconds * 1000).toLong()
                fastestInterval = 1000
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }
            startService()
        }
    }


    override fun onLocationResult(result: LocationResult) {
        val location = result.lastLocation
        if (!running) return
        val coordinates = Coordinates(
            latitude = Angle(location.latitude),
            longitude = Angle(location.longitude),
            altitude = location.altitude,
            timestamp = location.time
        )

        println("RESULT: $coordinates")
        this.route?.let { route ->
            val last = route.lastPosition
            if (last == null) {
                route.path.add(coordinates)
                routeObservable.onNext(route)
            } else {
                if (last.timestamp != coordinates.timestamp) {
                    route.path.add(coordinates)
                    routeObservable.onNext(route)
                }
            }
        }
    }

    private fun startService() {
        val intent = Intent(activity, LocationService::class.java)
        startLocationUpdates()
        activity.startService(intent)
    }

    private fun stopService() {
        val intent = Intent(activity, LocationService::class.java)
        activity.stopService(intent)
    }

    override fun resume() {
        running = true
    }

    override fun stop()  {
        running = false
        route?.let { route ->
            routeObservable.onNext(route)
        }
    }

    override fun end(): Route? {
        running = false
        locationClient.removeLocationUpdates(this)
        stopService()
        return route
    }

    private fun startLocationUpdates() {
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
        println("Start requesting")
        locationClient.requestLocationUpdates(locationRequest, this, Looper.getMainLooper())
    }

    companion object {
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
    }
}