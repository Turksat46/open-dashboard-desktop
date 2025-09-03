package de.turksat46.opendashboard

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.benasher44.uuid.uuidFrom
import com.juul.kable.Descriptor
import com.juul.kable.ExperimentalApi
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import dev.mcarr.usb.impl.SerialPortList
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
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


@OptIn(ExperimentalUuidApi::class)
private val SERVICE_UUID = Uuid.parse("00001819-0000-1000-8000-00805f9b34fb")
@OptIn(ExperimentalUuidApi::class)
private val CHARACTERISTIC_UUID = Uuid.parse("00002a67-0000-1000-8000-00805f9b34fb")
private const val DEVICE_NAME = "open::Device"

@OptIn(ExperimentalUuidApi::class, ExperimentalApi::class)
fun main() = application {
    var dashboardState by remember { mutableStateOf(DashboardState()) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "open::Dashboard",
        alwaysOnTop = true,
        undecorated = false,
        //state = WindowState(placement = WindowPlacement.Fullscreen),
    ) {
        App(dashboardState)
        LaunchedEffect(Unit) {
            println("Starting BLE background task...")

            val locationCharacteristic = characteristicOf(
                Uuid.parse(uuidString = SERVICE_UUID.toString()),
                Uuid.parse(
                    uuidString = CHARACTERISTIC_UUID.toString()
                )
            )

            while (isActive) {
                var peripheral: Peripheral? = null
                try {
                    dashboardState = dashboardState.copy(status = ConnectionStatus.SCANNING)
                    println("Scanning for '$DEVICE_NAME'...")
                    val scanStartTime = System.currentTimeMillis()
                    val scanner = Scanner {
                        filters {
                            match{
                                name = Filter.Name.Exact("Pixel 7 von Kerem")
                            }
                            // Alternative: service = Filter.Service(SERVICE_UUID)
                        }
                        logging {
                            engine = SystemLogEngine
                            level = Logging.Level.Events
                        }
                    }
                    val advertisement = scanner.advertisements.first()
                    val scanEndTime = System.currentTimeMillis()
                    println("==> Found device after ${(scanEndTime - scanStartTime) / 1000.0} seconds.")
                    peripheral = Peripheral(advertisement)

                     dashboardState = dashboardState.copy(status = ConnectionStatus.CONNECTING)
                    println("Found device '${peripheral.name}', connecting...")
                    val connectStartTime = System.currentTimeMillis()
                    peripheral.connect()
                    dashboardState = dashboardState.copy(status = ConnectionStatus.CONNECTED)
                    val connectEndTime = System.currentTimeMillis()
                    println("==> Connected after an additional ${(connectEndTime - connectStartTime) / 1000.0} seconds.")
                    println("Connected!")

                    launch {
                        println("Observing characteristic...")
                        peripheral.observe(locationCharacteristic)
                            .map { bytes ->
                                println("bytes: " + String(bytes))
                                val dataString = bytes.decodeToString()
                                try {
                                    val parts = dataString.split(',')
                                    if (parts.size == 3) {
                                        val latitude = parts[0].toDouble()
                                        val longitude = parts[1].toDouble()
                                        val speed = parts[2].toFloat()
                                        dashboardState = dashboardState.copy(locationData = LocationData(latitude, longitude, speed))
                                    } else {
                                        println("Error parsing string: Expected 3 parts, but got ${parts.size}")
                                        null // Ungültiges Format
                                    }
                                } catch (e: Exception) {
                                    println("Error parsing data: $dataString -> ${e.message}")
                                    null // Bei Fehler null zurückgeben
                                }
//                            }
//                            .collect { data ->
//                                if (data != null) {
//                                    // Verarbeite die empfangenen Daten
//                                    println("Received LocationData: Lat=${data.latitude}, Lon=${data.longitude}, Speed=${data.speed} m/s")
//                                    // Hier würdest du deinen UI-State aktualisieren:
//                                    // dashboardState = dashboardState.copy(locationData = data)
//                                }
                            }.collect { data ->
                                if (data != null) {
                                    println("Data: $data")
                                }
                            }

                    }


                    delay(2000)
                    val message = "Hello from Desktop!".toByteArray()
                    println("Sending message to characteristic...")
                    peripheral.write(locationCharacteristic, message)

                    peripheral.state.first { it !is State.Connected }
                    println("Device disconnected.")

                } catch (e: CancellationException) {
                    println("BLE task cancelled.")
                    break
                } catch (e: Exception) {
                    println("Error in BLE loop: ${e.message}")
                } finally {
                    peripheral?.disconnect()
                    dashboardState = dashboardState.copy(status = ConnectionStatus.DISCONNECTED, locationData = null)
                    println("Cleaned up connection. Retrying in 5 seconds...")
                    delay(5000)
                }
            }
        }
    }
}

