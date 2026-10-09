package com.example.emgcompanion.presentation

import com.example.emgcompanion.domain.MeasurementSample
import com.example.emgcompanion.domain.MeasurementSource

data class EmgUiState(
    val source: MeasurementSource = MeasurementSource.DEMO,
    val status: SessionStatus = SessionStatus.IDLE,
    val latestSample: MeasurementSample? = null,
    val samples: List<MeasurementSample> = emptyList(),
    val errorMessage: String? = null,
) {
    fun isActive(): Boolean = status == SessionStatus.CONNECTING || status == SessionStatus.STREAMING
}
