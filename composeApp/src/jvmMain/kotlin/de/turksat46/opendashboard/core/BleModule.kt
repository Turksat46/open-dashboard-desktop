package de.turksat46.opendashboard.core

import com.juul.kable.ExperimentalApi
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val speed: Float // in m/s
)

enum class ConnectionStatus {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED
}

data class DashboardState(
    val status: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val locationData: LocationData? = null
)

@OptIn(ExperimentalUuidApi::class, ExperimentalApi::class)
class BleModule{

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _dashboardState = MutableStateFlow(DashboardState())

    val dashboardState = _dashboardState.asStateFlow()

    private companion object {
        val SERVICE_UUID = Uuid.parse("00001819-0000-1000-8000-00805f9b34fb")
        val CHARACTERISTIC_UUID = Uuid.parse("00002a67-0000-1000-8000-00805f9b34fb")

        // Der Gerätename oder die Service-UUID wird für den Scan-Filter verwendet.
        // Anpassen, je nachdem, was zuverlässiger ist.
        const val DEVICE_NAME_FILTER = "Pixel 7 von Kerem"

        val LOCATION_CHARACTERISTIC = characteristicOf(
            Uuid.parse(uuidString = SERVICE_UUID.toString()),
            Uuid.parse(
                uuidString = CHARACTERISTIC_UUID.toString()
            )
        )
    }

    /**
     * Startet den BLE-Verbindungs- und Beobachtungsprozess.
     * Läuft in einer Endlosschleife, bis der Scope gecancelt wird.
     */
    fun start() {
        scope.launch {
            println("Starting BLE background task...")
            while (isActive) {
                var peripheral: Peripheral? = null
                try {
                    // 1. Scannen
                    _dashboardState.value = _dashboardState.value.copy(status = ConnectionStatus.SCANNING)
                    println("Scanning for '$DEVICE_NAME_FILTER'...")
                    peripheral = scanForDevice()
                    println("==> Found device.")

                    // 2. Verbinden
                    _dashboardState.value = _dashboardState.value.copy(status = ConnectionStatus.CONNECTING)
                    println("Connecting to '${peripheral.name}'...")
                    peripheral.connect()
                    _dashboardState.value = _dashboardState.value.copy(status = ConnectionStatus.CONNECTED)
                    println("==> Connected!")

                    // 3. Daten beobachten
                    launch { observeLocationData(peripheral) }

                    // Warten, bis die Verbindung abbricht
                    peripheral.state.first { it !is State.Connected }
                    println("Device disconnected.")

                } catch (e: CancellationException) {
                    println("BLE task cancelled.")
                    break // Schleife beenden, wenn der Scope gecancelt wird
                } catch (e: Exception) {
                    println("Error in BLE loop: ${e.message}")
                    // Bei Fehlern wird der finally-Block ausgeführt, der einen Neustart versucht
                } finally {
                    peripheral?.disconnect()
                    _dashboardState.value = DashboardState(status = ConnectionStatus.DISCONNECTED) // Reset state
                    println("Cleaned up connection. Retrying in 5 seconds...")
                    delay(5000)
                }
            }
        }
    }

    /**
     * Stoppt alle laufenden Coroutines in diesem Manager.
     * Sollte aufgerufen werden, wenn die Anwendung schließt.
     */
    fun stop() {
        println("Stopping BLE Manager...")
        scope.cancel()
    }

    private suspend fun scanForDevice(): Peripheral {
        val scanner = Scanner {
            filters {
                match {
                    name = Filter.Name.Exact(DEVICE_NAME_FILTER)
                }
                // Alternativ nach Service filtern, oft zuverlässiger:
                // service = Filter.Service(SERVICE_UUID)
            }
            logging {
                engine = SystemLogEngine
                level = Logging.Level.Events
            }
        }
        val advertisement = scanner.advertisements.first()
        return Peripheral(advertisement)
    }

    private suspend fun observeLocationData(peripheral: Peripheral) {
        println("Observing characteristic...")
        peripheral.observe(LOCATION_CHARACTERISTIC)
            .map { bytes -> parseLocationData(bytes) }
            .catch { e -> println("Error observing characteristic: ${e.message}") }
            .collect { locationData ->
                if (locationData != null) {
                    _dashboardState.value = _dashboardState.value.copy(locationData = locationData)
                    println("Received LocationData: Lat=${locationData.latitude}, Lon=${locationData.longitude}, Speed=${locationData.speed} m/s")
                }
            }
    }

    private fun parseLocationData(bytes: ByteArray): LocationData? {
        return try {
            val dataString = bytes.decodeToString()
            val parts = dataString.split(',')
            if (parts.size == 3) {
                LocationData(
                    latitude = parts[0].toDouble(),
                    longitude = parts[1].toDouble(),
                    speed = parts[2].toFloat()
                )
            } else {
                println("Error parsing string: Expected 3 parts, but got ${parts.size} in '$dataString'")
                null
            }
        } catch (e: Exception) {
            println("Error parsing data: ${bytes.decodeToString()} -> ${e.message}")
            null
        }
    }


}