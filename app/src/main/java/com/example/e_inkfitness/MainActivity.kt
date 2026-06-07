package com.example.e_inkfitness

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.e_inkfitness.core.sensor.GpsLocationProvider
import com.example.e_inkfitness.core.sensor.GpsState
import com.example.e_inkfitness.core.sensor.LocationCallback
import com.example.e_inkfitness.feature.bike.BikeScreen
import com.example.e_inkfitness.feature.bike.BikeViewModel
import com.example.e_inkfitness.feature.bike.ButtonClickCallbacks
import com.mudita.mmd.ThemeMMD

class MainActivity : ComponentActivity() {

    private lateinit var locationProvider: GpsLocationProvider
    private val bikeViewModel by viewModels<BikeViewModel>()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                bikeViewModel.onGpsStateChange(GpsState.DENIED)
            }else{
                locationProvider.start()
                bikeViewModel.onGpsStateChange(locationProvider.gpsState)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProvider = GpsLocationProvider(this, object : LocationCallback {
            override fun onLocation(location: Location, gpsState: GpsState) {
                bikeViewModel.onLocation(location, gpsState)
            }
        })

        setContent {
            ThemeMMD {
                BikeScreen(
                    bikeViewModel.uiState,
                    object : ButtonClickCallbacks {
                        override fun onPause() {
                            onPauseClicked()
                        }
                        override fun onResume() {
                            onResumeClicked()
                        }
                        override fun onStop() {
                            onStopClicked()
                        }
                    }
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

    override fun onResume() {
        super.onResume()

        val hasPermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            bikeViewModel.onGpsStateChange(GpsState.STOPPED)
            locationProvider.start()
        } else {
            bikeViewModel.onGpsStateChange(GpsState.DENIED)
            locationProvider.stop()
        }
    }

    fun onPauseClicked() {
        bikeViewModel.onPauseClicked()
        locationProvider.stop()
    }

    fun onResumeClicked() {
        bikeViewModel.onResumeClicked()
        locationProvider.start()
    }

    fun onStopClicked() {
        bikeViewModel.onStopClicked()
        locationProvider.stop()
    }
}


