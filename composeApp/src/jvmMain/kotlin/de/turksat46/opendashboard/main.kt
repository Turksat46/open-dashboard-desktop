package de.turksat46.opendashboard

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.turksat46.opendashboard.core.BleModule

fun main() = application {
    // Erstelle und merke dir eine Instanz des BleManagers.
    // Dies stellt sicher, dass sie nicht bei jeder Neukomposition neu erstellt wird.
    val bleManager = remember { BleModule() }

    // Verwende LaunchedEffect, um den Manager zu starten und zu stoppen,
    // wenn die Anwendung gestartet bzw. beendet wird.
    LaunchedEffect(bleManager) {
        bleManager.start()

        // onDispose wird aufgerufen, wenn die Composable-Komponente den Scope verlässt (z.B. App-Schließung).
        // Dies ist der perfekte Ort, um Ressourcen freizugeben.

    }

    // Beobachte den dashboardState aus dem BleManager.
    // `collectAsState` sorgt dafür, dass die UI bei jeder Zustandsänderung neu gezeichnet wird.
    val dashboardState by bleManager.dashboardState.collectAsState()

    Window(
        onCloseRequest = ::exitApplication,
        title = "open::Dashboard",
        alwaysOnTop = true,
        undecorated = false,
        //state = WindowState(placement = WindowPlacement.Fullscreen),
    ) {
        // Gib den aktuellen Zustand an deine UI-Komponente weiter.
        App(dashboardState)
    }
}

// HINWEIS: Die App-Komponente (dein UI-Code) bleibt unverändert.
// Sie erhält weiterhin ein `DashboardState`-Objekt.
// Beispiel:
// @Composable
// fun App(state: DashboardState) {
//     MaterialTheme {
//         // ... UI-Code, der auf state.status und state.locationData reagiert ...
//     }
// }

//@OptIn(ExperimentalUuidApi::class, ExperimentalApi::class)
//fun main() = application {
//    var dashboardState by remember { mutableStateOf(DashboardState()) }
//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "open::Dashboard",
//        alwaysOnTop = true,
//        undecorated = false,
//        //state = WindowState(placement = WindowPlacement.Fullscreen),
//    ) {
//        App(dashboardState)
//        LaunchedEffect(Unit) {
//            println("Starting BLE background task...")
//
//            val locationCharacteristic = characteristicOf(
//                Uuid.parse(uuidString = SERVICE_UUID.toString()),
//                Uuid.parse(
//                    uuidString = CHARACTERISTIC_UUID.toString()
//                )
//            )
//
//            while (isActive) {
//                var peripheral: Peripheral? = null
//                try {
//                    dashboardState = dashboardState.copy(status = ConnectionStatus.SCANNING)
//                    println("Scanning for '$DEVICE_NAME'...")
//                    val scanStartTime = System.currentTimeMillis()
//                    val scanner = Scanner {
//                        filters {
//                            match{
//                               name = Filter.Name.Exact("Pixel 7 von Kerem")
//                           }
//                            // Alternative: service = Filter.Service(SERVICE_UUID)
//                        }
//                        logging {
//                            engine = SystemLogEngine
//                            level = Logging.Level.Events
//                        }
//                    }
//                    val advertisement = scanner.advertisements.first()
//                    val scanEndTime = System.currentTimeMillis()
//                    println("==> Found device after ${(scanEndTime - scanStartTime) / 1000.0} seconds.")
//                    peripheral = Peripheral(advertisement)
//
//                     dashboardState = dashboardState.copy(status = ConnectionStatus.CONNECTING)
//                    println("Found device '${peripheral.name}', connecting...")
//                    val connectStartTime = System.currentTimeMillis()
//                    peripheral.connect()
//                    dashboardState = dashboardState.copy(status = ConnectionStatus.CONNECTED)
//                    val connectEndTime = System.currentTimeMillis()
//                    println("==> Connected after an additional ${(connectEndTime - connectStartTime) / 1000.0} seconds.")
//                    println("Connected!")
//
//                    launch {
//                        println("Observing characteristic...")
//                        peripheral.observe(locationCharacteristic)
//                            .map { bytes ->
//                                println("bytes: " + String(bytes))
//                                val dataString = bytes.decodeToString()
//                                try {
//                                    val parts = dataString.split(',')
//                                    if (parts.size == 3) {
//                                        val latitude = parts[0].toDouble()
//                                        val longitude = parts[1].toDouble()
//                                        val speed = parts[2].toFloat()
//                                        dashboardState = dashboardState.copy(locationData = LocationData(latitude, longitude, speed))
//                                    } else {
//                                        println("Error parsing string: Expected 3 parts, but got ${parts.size}")
//                                        null // Ungültiges Format
//                                    }
//                                } catch (e: Exception) {
//                                    println("Error parsing data: $dataString -> ${e.message}")
//                                    null // Bei Fehler null zurückgeben
//                                }
////                            }
////                            .collect { data ->
////                                if (data != null) {
////                                    // Verarbeite die empfangenen Daten
////                                    println("Received LocationData: Lat=${data.latitude}, Lon=${data.longitude}, Speed=${data.speed} m/s")
////                                    // Hier würdest du deinen UI-State aktualisieren:
////                                    // dashboardState = dashboardState.copy(locationData = data)
////                                }
//                            }.collect { data ->
//                                if (data != null) {
//                                    println("Data: $data")
//                                }
//                            }
//
//                    }
//
//
//                    delay(2000)
//                    val message = "Hello from Desktop!".toByteArray()
//                    println("Sending message to characteristic...")
//                    peripheral.write(locationCharacteristic, message)
//
//                    peripheral.state.first { it !is State.Connected }
//                    println("Device disconnected.")
//
//                } catch (e: CancellationException) {
//                    println("BLE task cancelled.")
//                    break
//                } catch (e: Exception) {
//                    println("Error in BLE loop: ${e.message}")
//                } finally {
//                    peripheral?.disconnect()
//                    dashboardState = dashboardState.copy(status = ConnectionStatus.DISCONNECTED, locationData = null)
//                    println("Cleaned up connection. Retrying in 5 seconds...")
//                    delay(5000)
//                }
//            }
//        }
//    }
//}

