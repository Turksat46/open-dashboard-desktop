package de.turksat46.opendashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kashif.cameraK.controller.CameraController
import de.turksat46.opendashboard.core.ConnectionStatus
import de.turksat46.opendashboard.core.DashboardState
import de.turksat46.opendashboard.modules.CameraCard
import de.turksat46.opendashboard.modules.SpeedCard
import de.turksat46.opendashboard.modules.Taskbar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Eine Hilfsfunktion, um zwischen verschiedenen Alignment-Werten flüssig zu animieren.
 */
@Composable
private fun animateAlignmentAsState(targetAlignment: Alignment): State<Alignment> {
    val biased = targetAlignment as BiasAlignment
    val horizontalBias by animateFloatAsState(biased.horizontalBias, animationSpec = tween(600))
    val verticalBias by animateFloatAsState(biased.verticalBias, animationSpec = tween(600))
    return derivedStateOf { BiasAlignment(horizontalBias, verticalBias) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(state: DashboardState) {
    MaterialTheme {
        var showConnectionStatusCard by remember { mutableStateOf(true) }

        LaunchedEffect(state.status) {
            if (state.status == ConnectionStatus.CONNECTED) {
                delay(3000L)
                showConnectionStatusCard = false
            } else {
                showConnectionStatusCard = true
            }
        }

        Box(
            modifier = Modifier
                .background(color = Color(0xFF121212))
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Status-Anzeige (Header)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                        Card(modifier = Modifier.padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E))) {
                            Text(
                                "open::Dashboard",
                                textAlign = TextAlign.Center,
                                fontSize = 30.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = showConnectionStatusCard,
                            enter = fadeIn(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(500))
                        ) {
                            val statusText = when (state.status) {
                                ConnectionStatus.DISCONNECTED -> "Getrennt. Suche in Kürze..."
                                ConnectionStatus.SCANNING -> "Suche nach Gerät..."
                                ConnectionStatus.CONNECTING -> "Verbinde..."
                                ConnectionStatus.CONNECTED -> "Verbunden"
                            }
                            Card(
                                modifier = Modifier.padding(16.dp).animateContentSize(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                                    Text(
                                        statusText,
                                        textAlign = TextAlign.Center,
                                        fontSize = 30.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                    if (state.status == ConnectionStatus.SCANNING || state.status == ConnectionStatus.CONNECTING) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Hauptinhalt je nach Verbindungsstatus
                    if(state.status != ConnectionStatus.CONNECTED) {
                        val text = when(state.status) {
                            ConnectionStatus.SCANNING -> "Bitte öffnen Sie die open::Dashboard-App"
                            ConnectionStatus.CONNECTING -> "Danke :D Die Verbindung wird hergestellt..."
                            else -> "Verbindung verloren :( Es wird gleich wieder gesucht!"
                        }
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(text, textAlign = TextAlign.Center, fontSize = 30.sp, color = Color.White)
                        }
                    } else {
                        // === LAYOUT FÜR DEN VERBUNDENEN ZUSTAND ===
                        var isCameraMaximized by remember { mutableStateOf(false) }
                        val cameraController = remember { mutableStateOf<CameraController?>(null) }
                        val isCameraReady by remember { derivedStateOf { cameraController.value != null } }

                        val blurRadius by animateDpAsState(if (isCameraMaximized) 16.dp else 0.dp, label = "BlurAnimation")
                        val alignment by animateAlignmentAsState(if (isCameraMaximized) Alignment.Center else Alignment.TopEnd)
                        val cardPadding: Dp by animateDpAsState(if(isCameraMaximized) 32.dp else 20.dp, label = "PaddingAnimation")

                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val screenWidth = this.maxWidth
                            val screenHeight = this.maxHeight

                            val animatedWidth by animateDpAsState(
                                targetValue = if (isCameraMaximized) screenWidth - (cardPadding * 2) else 240.dp,
                                animationSpec = tween(600), label = "WidthAnimation"
                            )
                            val animatedHeight by animateDpAsState(
                                targetValue = if (isCameraMaximized) screenHeight - (cardPadding * 2) else 180.dp,
                                animationSpec = tween(600), label = "HeightAnimation"
                            )

                            // Hintergrund-Inhalt (Tacho), der unscharf wird
                            SpeedCard(
                                speedKmh = (state.locationData?.speed?.times(3.6f))?.roundToInt() ?: 0,
                                modifier = Modifier
                                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                                    .blur(radius = blurRadius)
                            )

                            // Klickbarer Hintergrund, um Kamera zu schließen
                            if (isCameraMaximized) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { isCameraMaximized = false }
                                )
                            }

                            // Kamera-Platzhalter oder die eigentliche Kamera-Vorschau
                            if (isCameraReady) {
                                CameraCard(
                                    modifier = Modifier
                                        .align(alignment)
                                        .padding(cardPadding)
                                        .size(width = animatedWidth, height = animatedHeight)
                                        .zIndex(1f)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { isCameraMaximized = !isCameraMaximized },
                                    onCameraControllerReady = { cameraController.value  }
                                )
                            } else {
                                Card(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(20.dp)
                                        .size(width = 240.dp, height = 180.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }

                // Taskbar immer unten zentriert
                Taskbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) { /* Inhalt der Taskbar kann hier eingefügt werden */ }
            }
        }
    }
}