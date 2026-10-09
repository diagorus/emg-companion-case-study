package com.example.emgcompanion.presentation

import com.example.emgcompanion.data.MeasurementRepository
import com.example.emgcompanion.domain.MeasurementEvent
import com.example.emgcompanion.domain.MeasurementSample
import com.example.emgcompanion.domain.MeasurementSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EmgViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun mapsStreamToUiStateAndKeepsOnlyLatestWindow() = runTest(dispatcher) {
        val readings = (1..250).map { MeasurementEvent.Reading(MeasurementSample(it.toDouble())) }
        val repository = FixedRepository(flowOf(MeasurementEvent.Connected, *readings.toTypedArray()))
        val viewModel = EmgViewModel(repository)

        viewModel.start()
        advanceUntilIdle()

        assertEquals(SessionStatus.STREAMING, viewModel.uiState.value.status)
        assertEquals(250.0, viewModel.uiState.value.latestSample?.microvolts)
        assertEquals(240, viewModel.uiState.value.samples.size)
        assertEquals(11.0, viewModel.uiState.value.samples.first().microvolts)
    }

    @Test
    fun stopCancelsTheSourceAndReturnsToIdle() = runTest(dispatcher) {
        var sourceClosed = false
        val source = flow {
            emit(MeasurementEvent.Connected)
            awaitCancellation()
        }.onCompletion { cause ->
            sourceClosed = cause is CancellationException
        }
        val viewModel = EmgViewModel(FixedRepository(source))

        viewModel.start()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isActive())

        viewModel.stop()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isActive())
        assertEquals(SessionStatus.IDLE, viewModel.uiState.value.status)
        assertTrue(sourceClosed)
    }

    @Test
    fun protocolFailureStaysVisibleAndNeverSelectsDemo() = runTest(dispatcher) {
        val message = "Die Gerätespezifikation fehlt."
        val viewModel = EmgViewModel(FixedRepository(flowOf(MeasurementEvent.Failed(message))))
        viewModel.selectSource(MeasurementSource.DEVICE)

        viewModel.start()
        advanceUntilIdle()

        assertEquals(MeasurementSource.DEVICE, viewModel.uiState.value.source)
        assertEquals(SessionStatus.FAILED, viewModel.uiState.value.status)
        assertEquals(message, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun selectingAnotherSourceClearsPreviousSamples() = runTest(dispatcher) {
        val viewModel = EmgViewModel(
            FixedRepository(
                flowOf(
                    MeasurementEvent.Connected,
                    MeasurementEvent.Reading(MeasurementSample(123.0)),
                ),
            ),
        )
        viewModel.start()
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.samples.size)

        viewModel.selectSource(MeasurementSource.DEVICE)

        assertEquals(MeasurementSource.DEVICE, viewModel.uiState.value.source)
        assertEquals(null, viewModel.uiState.value.latestSample)
        assertTrue(viewModel.uiState.value.samples.isEmpty())
    }

    private class FixedRepository(private val source: Flow<MeasurementEvent>) : MeasurementRepository {
        override fun observe(source: MeasurementSource): Flow<MeasurementEvent> = this.source
    }
}
