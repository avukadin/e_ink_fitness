package com.example.e_inkfitness.feature.bike


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_inkfitness.core.model.Units
import com.example.e_inkfitness.core.sensor.GpsState
import com.example.e_inkfitness.core.tools.UnitConversion
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.divider.VerticalDividerMMD
import com.mudita.mmd.components.snackbar.SnackbarHostMMD
import com.mudita.mmd.components.snackbar.SnackbarHostStateMMD
import java.util.Locale

@Composable
fun BikeScreen(
    uiState: BikeUiState,
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

    val elapsedTime = formatElapsedTime(uiState.metrics.totalTime)
    val snackbarHostState = remember { SnackbarHostStateMMD() }

    LaunchedEffect(uiState.gpsState) {
        when (uiState.gpsState) {
            GpsState.DENIED -> {
                snackbarHostState.showSnackbar("GPS Permission Denied")
            }

            GpsState.WAITING -> {
                snackbarHostState.showSnackbar("Waiting for GPS Signal")
            }

            GpsState.LOW_ACCURACY -> {
                snackbarHostState.showSnackbar("GPS Signal Unavailable")
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
                    .padding(bottom = 56.dp)
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

                MetricBox(
                    value = elapsedTime,
                    sub1 = "Time",
                    modifier = Modifier.weight(1f),
                    valueFontSize = 52.sp
                )
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