package de.turksat46.opendashboard

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.mcarr.usb.impl.SerialPortList
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.runBlocking

fun main() = application {

// Open the device (and close automatically)


    Window(
        onCloseRequest = ::exitApplication,
        title = "open::Dashboard",
        alwaysOnTop = true,
        undecorated = true,
        //state = WindowState(placement = WindowPlacement.Fullscreen),
    ) {

        App()
//        runBlocking {
//            // Get available devices
//            val devices = SerialPortList().get()
//            println(devices)
//            // Grab the USB device you want
//            val device = devices.first()
//
//            device.use {
//                // Set the baud rate
//                it.setBaudRate(115200)
//
//                // Write some data to the port
//                it.write("test".toByteArray(), 10000)
//
//                // Read the response
//                val response = it.read(5, 10000)
//            }
//        }
    }



}