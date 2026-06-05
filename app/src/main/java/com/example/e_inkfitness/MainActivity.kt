package com.example.e_inkfitness

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.mudita.mmd.ThemeMMD
import java.util.Locale

class MainActivity : ComponentActivity(), LocationListener {

    private lateinit var locationManager: LocationManager

    private var speedText by mutableStateOf("--.-")
    private var statusText by mutableStateOf("Waiting for GPS...")

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startGps()
            } else {
                statusText = "Location permission denied"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setContent {
            ThemeMMD {
                SpeedScreen(
                    speedText = speedText,
                    statusText = statusText
                )
            }
        }

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startGps()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startGps() {
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gpsEnabled) {
            statusText = "GPS disabled"
            return
        }

        statusText = "Waiting for GPS..."

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                0f,
                this
            )
        } catch (e: SecurityException) {
            statusText = "Location permission missing"
        }
    }

    override fun onLocationChanged(location: Location) {
        val rawSpeedKmh = if (location.hasSpeed()) {
            location.speed * 3.6f
        } else {
            0f
        }

        val displaySpeedKmh = if (rawSpeedKmh < 1.0f) {
            0f
        } else {
            rawSpeedKmh
        }

        speedText = String.format(Locale.US, "%.1f", displaySpeedKmh)
        statusText = "km/h"
    }

    override fun onProviderEnabled(provider: String) {
        statusText = "Waiting for GPS..."
    }

    override fun onProviderDisabled(provider: String) {
        statusText = "GPS disabled"
        speedText = "--.-"
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }
}

@Composable
fun SpeedScreen(
    speedText: String,
    statusText: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = speedText,
                fontSize = 96.sp
            )

            Text(
                text = statusText,
                fontSize = 32.sp
            )
        }
    }
}