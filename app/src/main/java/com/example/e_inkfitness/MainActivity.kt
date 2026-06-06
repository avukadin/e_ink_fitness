package com.example.e_inkfitness

import BikeScreen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.e_inkfitness.core.sensor.GpsLocationProvider
import com.example.e_inkfitness.feature.bike.BikeViewModel
import com.mudita.mmd.ThemeMMD
import android.location.Location
import com.example.e_inkfitness.core.sensor.LocationCallback

class MainActivity : ComponentActivity() {

    private lateinit var locationProvider: GpsLocationProvider
    private val bikeViewModel by viewModels<BikeViewModel>()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                locationProvider.start()
            } else {
                // TODO
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProvider = GpsLocationProvider(this, object : LocationCallback {
            override fun onLocation(location: Location) {
                bikeViewModel.onLocation(location)
            }
        })

        setContent {
            ThemeMMD {
                BikeScreen(
                    bikeViewModel.metrics
                )
            }
        }

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationProvider.start()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationProvider.stop()
    }
}

