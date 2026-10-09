package com.example.emgcompanion.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emgcompanion.data.MeasurementRepository
import com.example.emgcompanion.domain.MeasurementEvent
import com.example.emgcompanion.domain.MeasurementSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmgViewModel(
    private val repository: MeasurementRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(EmgUiState())
    val uiState: StateFlow<EmgUiState> = mutableUiState.asStateFlow()

    private var collectionJob: Job? = null

    fun selectSource(source: MeasurementSource) {
        if (collectionJob?.isActive == true) return
        mutableUiState.update { current ->
            if (current.source == source) {
                current
            } else {
                current.copy(
                    source = source,
                    status = SessionStatus.IDLE,
                    latestSample = null,
                    samples = emptyList(),
                    errorMessage = null,
                )
            }
        }
    }

    fun start() {
        if (collectionJob?.isActive == true) return

        val source = mutableUiState.value.source
        mutableUiState.update {
            it.copy(
                status = SessionStatus.CONNECTING,
                latestSample = null,
                samples = emptyList(),
                errorMessage = null,
            )
        }

        collectionJob = viewModelScope.launch {
            try {
                repository.observe(source).collect(::handleEvent)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableUiState.update {
                    it.copy(
                        status = SessionStatus.FAILED,
                        errorMessage = "Messung konnte nicht gestartet werden.",
                    )
                }
            }
        }
    }

    fun stop() {
        collectionJob?.cancel()
        collectionJob = null
        mutableUiState.update {
            it.copy(
                status = SessionStatus.IDLE,
                errorMessage = null,
            )
        }
    }

    private fun handleEvent(event: MeasurementEvent) {
        mutableUiState.update { current ->
            when (event) {
                MeasurementEvent.Connecting -> current.copy(status = SessionStatus.CONNECTING)
                MeasurementEvent.Connected -> current.copy(status = SessionStatus.STREAMING)
                is MeasurementEvent.Reading -> current.copy(
                    status = SessionStatus.STREAMING,
                    latestSample = event.sample,
                    samples = (current.samples + event.sample).takeLast(MAX_VISIBLE_SAMPLES),
                )
                MeasurementEvent.Disconnected -> current.copy(status = SessionStatus.IDLE)
                is MeasurementEvent.Failed -> current.copy(
                    status = SessionStatus.FAILED,
                    errorMessage = event.message,
                )
            }
        }
    }

    private companion object {
        const val MAX_VISIBLE_SAMPLES = 240
    }
}
