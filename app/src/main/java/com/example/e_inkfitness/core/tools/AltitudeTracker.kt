import kotlin.math.abs

class AltitudeTracker {

    private var smoothedAltitude: Float? = null
    private var lastAcceptedAltitude: Float? = null
    private var totalAltitudeGain = 0f

    fun updateAltitude(altitude: Float) {
        val previousSmooth = smoothedAltitude
        if (previousSmooth == null) {
            smoothedAltitude = altitude
            lastAcceptedAltitude = altitude
            return
        }

        val smooth =
            previousSmooth + SMOOTHING_ALPHA * (altitude - previousSmooth)

        val last = lastAcceptedAltitude!!
        val delta = smooth - last
        if (abs(delta) >= MIN_ACCEPTED_CHANGE) {
            if (delta > 0f) {
                totalAltitudeGain += delta
            }
            lastAcceptedAltitude = smooth
        }
        smoothedAltitude = smooth
    }

    fun getTotalAltitudeGain(): Float = totalAltitudeGain

    fun getCurrentAltitude(): Float? = lastAcceptedAltitude

    companion object {
        private const val SMOOTHING_ALPHA = 0.15f
        private const val MIN_ACCEPTED_CHANGE = 0.5f
    }
}