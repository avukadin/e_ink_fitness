import com.example.e_inkfitness.core.model.BikeMetrics
import com.example.e_inkfitness.core.model.User
import com.example.e_inkfitness.core.sensor.GpsState


data class BikeUiState(
    val metrics: BikeMetrics,
    val user: User,
    val gpsState: GpsState,
    val isTracking: Boolean,
)