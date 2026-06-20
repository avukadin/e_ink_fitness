package com.example.e_inkfitness.core.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat

interface LocationCallback {
    fun onLocation(location: Location, gpsState: GpsState)
}

enum class GpsState {
    DENIED, DISABLED, WAITING, ACTIVE, LOW_ACCURACY, STOPPED
}

class GpsLocationProvider(
    private val context: Context,
    private val callback: LocationCallback
) : LocationListener {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE)
                as LocationManager
    var gpsState = GpsState.STOPPED

    var goodFixCount = 0;
    fun start() {
        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            gpsState = GpsState.DENIED
            return
        }
        startGps()
    }

    fun stop() {
        locationManager.removeUpdates(this)
        goodFixCount = 0
        gpsState = GpsState.STOPPED
    }

    private fun startGps() {
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gpsEnabled) {
            gpsState = GpsState.DISABLED
            return
        }

        gpsState = GpsState.WAITING

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                0f,
                this
            )
        } catch (e: SecurityException) {
            gpsState = GpsState.DENIED
        }
    }

    override fun onLocationChanged(location: Location) {
        if (location.hasAccuracy() && location.accuracy >= MAX_ACCURACY) {
            gpsState = GpsState.LOW_ACCURACY
            goodFixCount = 0
        } else {
            gpsState = GpsState.ACTIVE
            goodFixCount += 1
        }

        if (goodFixCount > MIN_FIX_COUNT) {
            callback.onLocation(location, gpsState)
        }
    }

    companion object {
        private const val MAX_ACCURACY = 15f
        private const val MIN_FIX_COUNT = 5
    }

}