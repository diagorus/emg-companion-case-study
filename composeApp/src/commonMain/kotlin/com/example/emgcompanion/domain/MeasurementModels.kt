package com.example.emgcompanion.domain

data class MeasurementSample(val microvolts: Double)

enum class MeasurementSource {
    DEMO,
    DEVICE,
}

sealed interface MeasurementEvent {
    data object Connecting : MeasurementEvent
    data object Connected : MeasurementEvent
    data class Reading(val sample: MeasurementSample) : MeasurementEvent
    data object Disconnected : MeasurementEvent
    data class Failed(val message: String) : MeasurementEvent
}
