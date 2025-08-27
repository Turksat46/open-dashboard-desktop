package de.turksat46.opendashboard.communication

import de.turksat46.opendashboard.common.LocationData
import kotlinx.coroutines.flow.Flow

// Interface für das Handy (Sender/Peripheral)
interface BleDataSender {
    fun startAdvertising(dataFlow: Flow<LocationData>)
    fun stopAdvertising()
}

// Interface für den Raspberry Pi (Empfänger/Central)
interface BleDataReader {
    fun startScan(): Flow<LocationData>
    fun stopScan()
}

// Factory-Funktionen für einfachen Zugriff
// fun getBleDataSender(): BleDataSender
// fun getBleDataReader(): BleDataReader