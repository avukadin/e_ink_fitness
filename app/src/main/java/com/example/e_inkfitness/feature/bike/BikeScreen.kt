package com.example.e_inkfitness.feature.bike


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_inkfitness.core.model.Units
import com.example.e_inkfitness.core.sensor.GpsState
import com.example.e_inkfitness.core.tools.UnitConversion
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.divider.VerticalDividerMMD
import com.mudita.mmd.components.snackbar.SnackbarDurationMMD
import com.mudita.mmd.components.snackbar.SnackbarHostMMD
import com.mudita.mmd.components.snackbar.SnackbarHostStateMMD
import java.util.Locale


interface ButtonClickCallbacks {
    fun onPause()
    fun onStop()
    fun onResume()
}


@Composable
fun BikeScreen(
    uiState: BikeUiState,
    buttonClickCallbacks: ButtonClickCallbacks
) {

    val speed = UnitConversion.convertSpeed(
        uiState.metrics.speed,
        uiState.user.units
    )

    val avgSpeed = UnitConversion.convertSpeed(
        uiState.metrics.avgRollingSpeed,
        uiState.user.units
    )

    val distance = UnitConversion.convertDistance(
        uiState.metrics.distance,
        uiState.user.units
    )

    val speedUnit = when (uiState.user.units) {
        Units.METRIC -> "km/h"
        Units.IMPERIAL -> "mph"
    }

    val distanceUnit = when (uiState.user.units) {
        Units.METRIC -> "km"
        Units.IMPERIAL -> "mi"
    }

    val elevationGain = UnitConversion.convertElevation(
        uiState.metrics.elevationGain,
        uiState.user.units
    )

    val elevationUnit = when (uiState.user.units) {
        Units.METRIC -> "m"
        Units.IMPERIAL -> "ft"
    }

    val elapsedTime = formatElapsedTime(uiState.metrics.totalTime)
    val snackbarHostState = remember { SnackbarHostStateMMD() }

    LaunchedEffect(uiState.gpsState) {
        when (uiState.gpsState) {
            GpsState.DENIED -> {
                snackbarHostState.showSnackbar(
                    "GPS Permission Denied",
                    duration = SnackbarDurationMMD.Indefinite
                )
            }

            GpsState.WAITING -> {
                snackbarHostState.showSnackbar(
                    "Waiting for GPS Signal",
                    duration = SnackbarDurationMMD.Indefinite
                )
            }

            GpsState.LOW_ACCURACY -> {
                snackbarHostState.showSnackbar(
                    "GPS Signal Unavailable",
                    duration = SnackbarDurationMMD.Indefinite
                )

            }

            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHostMMD(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .padding(bottom = 35.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    MetricBox(
                        value = String.format(Locale.US, "%.1f", speed),
                        sub1 = speedUnit,
                        modifier = Modifier.weight(1f)
                    )

                    VerticalDividerMMD(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 8.dp)
                    )

                    MetricBox(
                        value = String.format(Locale.US, "%.1f", distance),
                        sub1 = distanceUnit,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDividerMMD(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    MetricBox(
                        value = String.format(Locale.US, "%.1f", avgSpeed),
                        sub1 = "$speedUnit (avg)",
                        modifier = Modifier.weight(1f)
                    )

                    VerticalDividerMMD(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 8.dp)
                    )

                    MetricBox(
                        value = uiState.metrics.calories.toInt().toString(),
                        sub1 = "calories",
                        modifier = Modifier.weight(1f),
                    )
                }

                HorizontalDividerMMD(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    MetricBox(
                        value = String.format(Locale.US, "%.0f", elevationGain),
                        sub1 = "elevation gain ($elevationUnit)",
                        modifier = Modifier.weight(1f)
                    )

                    VerticalDividerMMD(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 8.dp)
                    )

                    MetricBox(
                        value = elapsedTime,
                        sub1 = "time",
                        modifier = Modifier.weight(1f),
                        valueFontSize = 42.sp
                    )
                }

                Row(
                    modifier = Modifier.weight(0.7f),
                ) {
                    Controls(
                        activityState = uiState.activityState,
                        buttonClickCallbacks = buttonClickCallbacks,
                    )
                }

            }
        }

    }

}

@Composable
private fun MetricBox(
    value: String,
    sub1: String,
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = 56.sp
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = valueFontSize
            )
            Text(
                text = sub1,
                fontSize = 18.sp
            )
        }
    }
}


@Composable
private fun Controls(
    modifier: Modifier = Modifier,
    iconSize: Dp = 74.dp,
    activityState: ActivityState,
    buttonClickCallbacks: ButtonClickCallbacks,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp)
            .padding(bottom = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (activityState == ActivityState.PAUSED || activityState == ActivityState.STOPPED) {
                IconButton(
                    onClick = {
                        buttonClickCallbacks.onResume()
                    },
                    modifier = Modifier
                        .size(iconSize)
                        .fillMaxSize(),
                ) {

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier
                            .size(iconSize)
                            .fillMaxSize(),
                    )
                }
            } else if (activityState == ActivityState.ACTIVE) {
                IconButton(
                    onClick = { buttonClickCallbacks.onPause() },
                    modifier = Modifier
                        .size(iconSize)
                        .fillMaxSize(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        modifier = Modifier
                            .size(iconSize)
                            .fillMaxSize(),
                    )
                }
            }


            IconButton(
                onClick = { buttonClickCallbacks.onStop() },
                modifier = Modifier
                    .size(iconSize)
                    .fillMaxSize(),
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    modifier = Modifier
                        .size(iconSize)
                        .fillMaxSize(),
                )
            }

            IconButton(
                onClick = { buttonClickCallbacks.onStop() },
                modifier = Modifier
                    .size(iconSize)
                    .fillMaxSize(),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(iconSize)
                        .fillMaxSize(),
                )
            }
        }
    }
}

fun formatElapsedTime(seconds: Float): String {
    val totalSeconds = seconds.toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return String.format(
        locale = Locale.US,
        "%02d:%02d:%02d",
        hours,
        minutes,
        secs
    )
}