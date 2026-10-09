package com.example.emgcompanion.data

import com.example.emgcompanion.domain.MeasurementEvent
import com.example.emgcompanion.domain.MeasurementSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MeasurementRepository {
    fun observe(source: MeasurementSource): Flow<MeasurementEvent>
}

class DefaultMeasurementRepository(
    private val sources: Map<MeasurementSource, MeasurementDataSource>,
) : MeasurementRepository {
    override fun observe(source: MeasurementSource): Flow<MeasurementEvent> {
        val dataSource = sources[source]
        return if (dataSource != null) {
            dataSource.events()
        } else {
            flow {
                emit(MeasurementEvent.Failed("Diese Messquelle ist nicht konfiguriert."))
            }
        }
    }
}

fun createDefaultMeasurementRepository(): MeasurementRepository = DefaultMeasurementRepository(
    sources = mapOf(
        MeasurementSource.DEMO to StubMeasurementDataSource(),
        MeasurementSource.DEVICE to KableBleMeasurementDataSource(protocol = null),
    ),
)
