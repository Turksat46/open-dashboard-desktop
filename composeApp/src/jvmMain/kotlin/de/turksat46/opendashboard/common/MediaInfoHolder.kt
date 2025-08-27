package de.turksat46.opendashboard.common

// Singleton oder ViewModel, um Daten an die UI zu übergeben
object MediaInfoHolder {

}

data class TrackInfo(
    val title: String?,
    val artist: String?,
    val album: String?, // Kann nützlich sein, auch wenn nicht direkt angezeigt
    val packageName: String, // Um die Quelle zu identifizieren

)
