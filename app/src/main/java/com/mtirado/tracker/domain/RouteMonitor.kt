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

class RouteMonitor(val locationClient: FusedLocationProviderClient, val activity: Activity, override val onValue: (Coordinates) -> Unit) : Monitor<Coordinates>, LocationCallback() {
    private var coordinates: Coordinates? = null
    private var running = false
    private var interval: Int = 1

    private lateinit var locationRequest: LocationRequest

    override val isRunning: Boolean get() = running

    override fun start(intervalInSeconds: Int) {
        this.interval = intervalInSeconds
        if (!running) {
            running = true

            locationRequest = LocationRequest.create().apply {
                interval = (intervalInSeconds * 1000).toLong()
                fastestInterval = 100
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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

        onValue(coordinates)
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
    }

    override fun end() {
        running = false
        locationClient.removeLocationUpdates(this)
        stopService()
        coordinates = null
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