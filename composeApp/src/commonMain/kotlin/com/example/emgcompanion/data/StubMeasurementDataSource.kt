package com.example.emgcompanion.data

import com.example.emgcompanion.domain.MeasurementEvent
import com.example.emgcompanion.domain.MeasurementSample
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.sin

/** Deterministic synthetic signal for UI development and demonstrations. */
class StubMeasurementDataSource(
    private val intervalMillis: Long = DEFAULT_INTERVAL_MILLIS,
) : MeasurementDataSource {
    init {
        require(intervalMillis > 0) { "The demo interval must be positive." }
    }

    override fun events(): Flow<MeasurementEvent> = flow {
        emit(MeasurementEvent.Connecting)
        delay(CONNECTION_DELAY_MILLIS)
        emit(MeasurementEvent.Connected)

        var sampleNumber = 0
        while (true) {
            val slowWave = sin(sampleNumber / 13.0) * 360.0
            val fastWave = sin(sampleNumber / 2.7) * 110.0
            val deterministicNoise = ((sampleNumber * 37) % 61 - 30) * 2.0
            emit(
                MeasurementEvent.Reading(
                    MeasurementSample(slowWave + fastWave + deterministicNoise),
                ),
            )
            sampleNumber += 1
            delay(intervalMillis)
        }
    }

    private companion object {
        const val CONNECTION_DELAY_MILLIS = 180L
        const val DEFAULT_INTERVAL_MILLIS = 50L
    }
}
