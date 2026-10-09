package com.example.emgcompanion.data

import com.example.emgcompanion.domain.MeasurementEvent
import com.example.emgcompanion.domain.MeasurementSource
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MeasurementDataSourceTest {
    @Test
    fun stubEmitsConnectionAndRepeatableSyntheticSamples() = runTest {
        val firstRun = StubMeasurementDataSource().events().take(5).toList()
        val secondRun = StubMeasurementDataSource().events().take(5).toList()

        assertEquals(MeasurementEvent.Connecting, firstRun[0])
        assertEquals(MeasurementEvent.Connected, firstRun[1])
        assertEquals(firstRun, secondRun)
        assertTrue(firstRun.drop(2).all { it is MeasurementEvent.Reading })
    }

    @Test
    fun unconfiguredBleReportsReasonAndDoesNotFallBackToDemo() = runTest {
        val repository = DefaultMeasurementRepository(
            mapOf(
                MeasurementSource.DEMO to StubMeasurementDataSource(),
                MeasurementSource.DEVICE to KableBleMeasurementDataSource(protocol = null),
            ),
        )

        val events = repository.observe(MeasurementSource.DEVICE).toList()

        assertEquals(1, events.size)
        assertTrue(events.single() is MeasurementEvent.Failed)
        assertTrue((events.single() as MeasurementEvent.Failed).message.contains("Gerätespezifikation"))
    }
}
