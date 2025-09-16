package de.turksat46.opendashboard.commonComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import opendashboard.composeapp.generated.resources.Res
import opendashboard.composeapp.generated.resources.baseline_pause_24
import opendashboard.composeapp.generated.resources.baseline_skip_next_24
import opendashboard.composeapp.generated.resources.baseline_skip_previous_24
import java.nio.ByteBuffer

// 1. Datenmodell für Medieninformationen
@Serializable
data class MediaData(
    val title: String,
    val artist: String,
    val albumArtBase64: String?, // Album-Cover als Base64-String
    val durationMillis: Long,
    val progressMillis: Long,
    val isPlaying: Boolean,
    val packageName: String,
    val canControl: Boolean
)



// 2. Der Hauptcontainer für den Mediaplayer
@Composable
fun InfoPanelMedia(modifier: Modifier = Modifier, mediaData: MediaData?) {
    // Merken uns das dekodierte Bild, um es nicht bei jeder Neuzusammensetzung neu zu dekodieren


    Card(
        modifier = modifier.animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMedium)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            // Inhalt des Media Players
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Titel und Künstler
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = mediaData?.title ?: "Keine Wiedergabe",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = mediaData?.artist ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }

                // Fortschrittsanzeige und Steuerung
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Slider
                    MediaProgressSlider(
                        durationMillis = mediaData?.durationMillis ?: 0,
                        progressMillis = mediaData?.progressMillis ?: 0,
                        onScrub = { /* TODO: Sende Scrub-Event an Handy */ }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Buttons
                    MediaControls(
                        isPlaying = mediaData?.isPlaying ?: false,
                        onPlay = { /* TODO: Sende Play-Event */ },
                        onPause = { /* TODO: Sende Pause-Event */ },
                        onSkipNext = { /* TODO: Sende Next-Event */ },
                        onSkipPrevious = { /* TODO: Sende Prev-Event */ },
                        canControl = mediaData?.canControl ?: false
                    )
                }
            }
        }
    }
}

// 3. Der benutzerdefinierte "Material You Expressive" Slider (KORRIGIERT)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaProgressSlider(
    durationMillis: Long,
    progressMillis: Long,
    onScrub: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Der Fortschritt als Wert zwischen 0.0 und 1.0
    val progress = if (durationMillis > 0) {
        (progressMillis.toFloat() / durationMillis.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }


    // Hilfsfunktion zum Formatieren der Zeit
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = progress,
            onValueChange = onScrub,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            ),
            thumb = {
                // Größerer "Daumen"
                Box(
                    modifier = Modifier.size(20.dp).background(Color.White, CircleShape)
                )
            },
            // HIER IST DIE KORREKTUR:
            track = {
                val trackHeight = 8.dp
                val activeTrackColor = Color.White
                val inactiveTrackColor = Color.White.copy(alpha = 0.3f)

                Canvas(modifier = Modifier.fillMaxWidth().height(trackHeight)) {
                    val isRtl = layoutDirection == LayoutDirection.Rtl
                    val sliderLeft = Offset(0f, center.y)
                    val sliderRight = Offset(size.width, center.y)
                    val sliderStart = if (isRtl) sliderRight else sliderLeft
                    val sliderEnd = if (isRtl) sliderLeft else sliderRight

                    // Manuelle Berechnung der Daumen-Position in Pixeln.
                    // 'progress' ist der Wert des Sliders (0.0f bis 1.0f).
                    // size.width ist die Gesamtbreite des Canvas in Pixeln.
                    val thumbPx = progress * size.width // <-- DAS IST DIE KORREKTUR

                    // Inaktive Spur (zeichnet die volle Länge)
                    drawLine(
                        color = inactiveTrackColor,
                        start = sliderStart,
                        end = sliderEnd,
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Aktive Spur (zeichnet bis zur berechneten Daumen-Position)
                    drawLine(
                        color = activeTrackColor,
                        start = sliderStart,
                        end = Offset(thumbPx, center.y),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(progressMillis), style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
            Text(formatTime(durationMillis), style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
        }
    }
}


// 4. Die "Material You Expressive" Steuerungsbuttons
@Composable
fun MediaControls(
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    canControl: Boolean,
) {
    val buttonColor = Color.White.copy(alpha = 0.15f)
    val iconColor = Color.White

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Skip Previous Button
        Surface(
            onClick = onSkipPrevious,
            enabled = canControl,
            shape = RoundedCornerShape(16.dp),
            color = buttonColor,
            contentColor = iconColor
        ) {
            Icon(
                painterResource(Res.drawable.baseline_skip_previous_24),
                "Vorheriger",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp).size(28.dp)
            )
        }

        // Play/Pause Button (größer und rund)
        Surface(
            onClick = if (isPlaying) onPause else onPlay,
            enabled = canControl,
            shape = CircleShape,
            color = Color.White, // Prominenter Hintergrund
            contentColor = Color.Black // Kontrastierendes Icon
        ) {
            Icon(
                imageVector = (if (isPlaying) painterResource(Res.drawable.baseline_pause_24) else Icons.Default.PlayArrow) as ImageVector,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.padding(20.dp).size(36.dp)
            )
        }

        // Skip Next Button
        Surface(
            onClick = onSkipNext,
            enabled = canControl,
            shape = RoundedCornerShape(16.dp),
            color = buttonColor,
            contentColor = iconColor
        ) {
            Icon(
                painterResource(Res.drawable.baseline_skip_next_24),
                "Nächster",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp).size(28.dp)
            )
        }
    }
}