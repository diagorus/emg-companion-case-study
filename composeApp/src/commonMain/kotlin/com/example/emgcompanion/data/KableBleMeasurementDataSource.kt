package com.example.emgcompanion.data

import com.example.emgcompanion.domain.MeasurementEvent
import com.example.emgcompanion.domain.MeasurementSample
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.characteristicOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.uuid.Uuid

fun interface EmgPacketDecoder {
    /** Decodes a device-protocol packet into zero or more samples, in microvolts. */
    fun decode(packet: ByteArray): List<Double>
}

data class BleProtocolConfiguration(
    val serviceUuid: String,
    val measurementCharacteristicUuid: String,
    val decoder: EmgPacketDecoder,
)

/** Kable transport adapter. Protocol identifiers and packet interpretation are injected. */
class KableBleMeasurementDataSource(
    private val protocol: BleProtocolConfiguration?,
) : MeasurementDataSource {
    override fun events(): Flow<MeasurementEvent> = flow {
        val configuration = protocol
        if (configuration == null) {
            emit(
                MeasurementEvent.Failed(
                    "Die Gerätespezifikation fehlt. BLE ist in diesem Build nicht konfiguriert.",
                ),
            )
            return@flow
        }

        emit(MeasurementEvent.Connecting)
        val serviceUuid: Uuid
        val characteristicUuid: Uuid
        val peripheral: Peripheral
        try {
            serviceUuid = Uuid.parse(configuration.serviceUuid)
            characteristicUuid = Uuid.parse(configuration.measurementCharacteristicUuid)
            val advertisement = withTimeoutOrNull(SCAN_TIMEOUT_MILLIS) {
                Scanner {
                    filters {
                        match {
                            services = listOf(serviceUuid)
                        }
                    }
                }.advertisements.first()
            }
            if (advertisement == null) {
                emit(MeasurementEvent.Failed("Kein passendes BLE-Gerät in der Suchzeit gefunden."))
                return@flow
            }
            peripheral = Peripheral(advertisement)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            emit(MeasurementEvent.Failed("BLE-Suche fehlgeschlagen. Prüfe Bluetooth und Berechtigungen."))
            return@flow
        }

        try {
            peripheral.connect()
            emit(MeasurementEvent.Connected)
            val characteristic = characteristicOf(
                service = serviceUuid,
                characteristic = characteristicUuid,
            )
            peripheral.observe(characteristic).collect { packet ->
                configuration.decoder.decode(packet).forEach { value ->
                    if (value.isFinite()) {
                        emit(MeasurementEvent.Reading(MeasurementSample(value)))
                    }
                }
            }
            emit(MeasurementEvent.Disconnected)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            emit(MeasurementEvent.Failed("BLE-Verbindung oder Messdatenstrom wurde unterbrochen."))
        } finally {
            withContext(NonCancellable) {
                withTimeoutOrNull(DISCONNECT_TIMEOUT_MILLIS) {
                    runCatching { peripheral.disconnect() }
                }
                peripheral.close()
            }
        }
    }

    private companion object {
        const val SCAN_TIMEOUT_MILLIS = 15_000L
        const val DISCONNECT_TIMEOUT_MILLIS = 5_000L
    }
}
