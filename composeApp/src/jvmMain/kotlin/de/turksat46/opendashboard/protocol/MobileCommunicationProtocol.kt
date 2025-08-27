package de.turksat46.opendashboard.protocol

// COMMUNICATION-PROTOCOL FOR DEVICE-COMMUNICATION
// Please see open::dashboard-wiki for more information
// The device will send packets as a ByteArray to communicate with the mobile device.
// An initial handshake will be made to exchange information such as device info, system versions etc.
// If there is a newer version for the desktop unit, the new firmware will be sent by the mobile device, if the
// desktop-unit doesn't have an internet connection.
// After that, the device will send a ready-packet and will only listen for packets
// The device will send an OK-packet every second, so that the mobile device can continue to send data
// The packets itself from the mobile device will only send increment data, such as +-1 kmh or the difference of gps data
// It should be very fast, we can and will set a timeline, in which packets are allowed to be sent.
// We can set a ping-pong connection, if the mobile device decides to do so for scenarios such as connection mid-drive
// or instable connection because of a bad wire. The mobile device can change that decision later.

class MobileCommunicationProtocol {
}