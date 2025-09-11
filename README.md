# open::Dashboard::Desktop

Get your driving stats and infotainment on desktop.       
This project allows to projectile the neccessary information to a bigger screen and outside the device.        
It can be on a raspberry pi or any system, which can run and handle JVM.

## THIS IS A WORK-IN-PROGRESS

It currently shows data in a broken matter


BIG TO-DO'S:
- Implementation of open::API
- Serialization of data delivery
- 

This is a Kotlin Multiplatform project targeting Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
