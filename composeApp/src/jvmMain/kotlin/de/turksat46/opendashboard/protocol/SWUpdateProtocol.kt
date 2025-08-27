package de.turksat46.opendashboard.protocol

// SOFTWARE-UPGRADE-COMMUNICATION-PROTOCOL
// Because we can't know if the device will be online or offline, the program has to know if it is the newest software
// available to the system.
// Some software upgrades will be critical, so we will communicate the importance of the update and force the user to upgrade
// In the upgrade progress, we will get the entirety of the programm, which shouldn't be large and be quick
// This can be improved tho' and maybe I can change the differences, but for now, I will replace the entire software

class SWUpdateProtocol {
}