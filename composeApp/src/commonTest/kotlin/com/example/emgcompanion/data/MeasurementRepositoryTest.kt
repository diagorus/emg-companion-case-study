package com.example.emgcompanion.data

import com.example.emgcompanion.domain.MeasurementEvent
import com.example.emgcompanion.domain.MeasurementSample
import com.example.emgcompanion.domain.MeasurementSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MeasurementRepositoryTest {
    @Test
    fun routesEventsFromTheSelectedSourceOnly() = runTest {
        val deviceEvents = listOf(
            MeasurementEvent.Connected,
            MeasurementEvent.Reading(MeasurementSample(42.0)),
        )
        val repository = DefaultMeasurementRepository(
            mapOf(
                MeasurementSource.DEMO to StubMeasurementDataSource(),
                MeasurementSource.DEVICE to FixedDataSource(deviceEvents),
            ),
        )

        assertEquals(deviceEvents, repository.observe(MeasurementSource.DEVICE).toList())
    }

    private class FixedDataSource(private val values: List<MeasurementEvent>) : MeasurementDataSource {
        override fun events(): Flow<MeasurementEvent> = flowOf(*values.toTypedArray())
    }
}
