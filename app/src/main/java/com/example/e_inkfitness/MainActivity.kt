package com.example.e_inkfitness

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.e_inkfitness.core.sensor.AltitudeCallback
import com.example.e_inkfitness.core.sensor.AltitudeProvider
import com.example.e_inkfitness.core.sensor.AltitudeSample
import com.example.e_inkfitness.core.sensor.GpsLocationProvider
import com.example.e_inkfitness.core.sensor.GpsState
import com.example.e_inkfitness.core.sensor.LocationCallback
import com.example.e_inkfitness.feature.bike.ActivityState
import com.example.e_inkfitness.feature.bike.BikeScreen
import com.example.e_inkfitness.feature.bike.BikeViewModel
import com.example.e_inkfitness.feature.bike.ButtonClickCallbacks
import com.mudita.mmd.ThemeMMD

class MainActivity : ComponentActivity() {

    private lateinit var locationProvider: GpsLocationProvider
    private lateinit var altitudeProvider: AltitudeProvider
    private val bikeViewModel by viewModels<BikeViewModel>()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                bikeViewModel.onGpsStateChange(GpsState.DENIED)
            } else {
                locationProvider.start()
                bikeViewModel.onGpsStateChange(locationProvider.gpsState)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        altitudeProvider = AltitudeProvider(this, object : AltitudeCallback {
            override fun onAltitude(altitudeSample: AltitudeSample) {
                bikeViewModel.onAltitudeChange(altitudeSample)
            }
        })

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

                        override fun onSettings() {
                            // settings screen not yet implemented
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
        altitudeProvider.stop()
    }

    override fun onResume() {
        super.onResume()

        val hasPermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        // Only restart GPS if the ride is active; paused/stopped rides stay stopped
        if (hasPermission && bikeViewModel.uiState.activityState == ActivityState.ACTIVE) {
            locationProvider.start()
            bikeViewModel.onGpsStateChange(locationProvider.gpsState)
        } else if (!hasPermission) {
            bikeViewModel.onGpsStateChange(GpsState.DENIED)
            locationProvider.stop()
        }

        altitudeProvider.start()
    }

    override fun onPause() {
        super.onPause()
        altitudeProvider.stop()
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

    override fun onStop() {
        // Stop GPS but keep lastLocation and activity state intact so the gap can be estimated
        // on the next location update when the app regains focus
        if (bikeViewModel.uiState.activityState == ActivityState.ACTIVE) {
            locationProvider.stop()
        }
        super.onStop()
    }
}
