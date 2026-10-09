package com.example.emgcompanion.data

import com.example.emgcompanion.domain.MeasurementEvent
import kotlinx.coroutines.flow.Flow

/** Emits device-neutral measurement and connection events for one collection session. */
interface MeasurementDataSource {
    fun events(): Flow<MeasurementEvent>
}
