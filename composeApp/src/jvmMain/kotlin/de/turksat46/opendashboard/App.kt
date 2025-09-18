package de.turksat46.opendashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints // WICHTIGER IMPORT
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.animation.core.animateIntAsState
import com.kashif.cameraK.controller.CameraController
import com.kashif.cameraK.enums.CameraLens
import com.kashif.cameraK.enums.Directory
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.enums.ImageFormat
import com.kashif.cameraK.enums.QualityPrioritization
import com.kashif.cameraK.ui.CameraPreview

import de.turksat46.opendashboard.core.ConnectionStatus
import de.turksat46.opendashboard.core.DashboardState

import opendashboard.composeapp.generated.resources.Res
import opendashboard.composeapp.generated.resources.baseline_pause_24
import opendashboard.composeapp.generated.resources.baseline_skip_next_24
import opendashboard.composeapp.generated.resources.baseline_skip_previous_24
import opendashboard.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.skia.Bitmap
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
@Preview
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
                    // ... (Der Code für die Status-Anzeige bleibt unverändert)
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

                    if(state.status == ConnectionStatus.SCANNING || state.status == ConnectionStatus.CONNECTING || state.status == ConnectionStatus.DISCONNECTED) {
                        // ... (Der Code für die nicht-verbundenen Zustände bleibt unverändert)
                        val text = when(state.status) {
                            ConnectionStatus.SCANNING -> "Bitte öffnen Sie die open::Dashboard-App"
                            ConnectionStatus.CONNECTING -> "Danke :D Die Verbindung wird hergestellt..."
                            else -> "Verbindung verloren :( Es wird gleich wieder gesucht!"
                        }
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(text, textAlign = TextAlign.Center, fontSize = 30.sp, color = Color.White)
                        }
                    } else {
                        // LAYOUT FÜR DEN VERBUNDENEN ZUSTAND MIT ANIMIERBARER KARTE
                        var isCameraMaximized by remember { mutableStateOf(false) }
                        val cameraController = remember { mutableStateOf<CameraController?>(null) }

                        val isCameraReady by remember { derivedStateOf { cameraController.value != null } }


                        // Animationen für die Zustandsänderungen
                        val blurRadius by animateDpAsState(if (isCameraMaximized) 16.dp else 0.dp, label = "BlurAnimation")
                        val alignment by animateAlignmentAsState(if (isCameraMaximized) Alignment.Center else Alignment.TopEnd)
                        val cardPadding: Dp by animateDpAsState(if(isCameraMaximized) 32.dp else 20.dp, label = "PaddingAnimation")

                        // === START DER ÄNDERUNG ===
                        // BoxWithConstraints misst den verfügbaren Platz und stellt ihn als maxWidth/maxHeight bereit
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
                            // === ENDE DER ÄNDERUNG ===


                            // Hintergrund-Inhalt (Tacho), der unscharf wird
                            Row(
                                modifier = Modifier
                                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                                    .blur(radius = blurRadius),
                                verticalAlignment = Alignment.Top
                            ) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                                    modifier = Modifier.animateContentSize()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val targetSpeedKmh = (state.locationData?.speed?.times(3.6f))?.roundToInt() ?: 0
                                        val animatedSpeedKmh by animateIntAsState(
                                            targetValue = targetSpeedKmh,
                                            animationSpec = tween(durationMillis = 700),
                                            label = "SpeedAnimation"
                                        )
                                        Text(
                                            text = animatedSpeedKmh.toString(), color = Color.White,
                                            fontSize = 70.sp, fontWeight = FontWeight.Bold
                                        )
                                        Text(text = "km/h", color = Color.LightGray, fontSize = 16.sp)
                                    }
                                }
                            }

                            // Klickbarer Hintergrund, der nur sichtbar ist, wenn die Kamera maximiert ist, um sie zu schließen
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


                            // Bedingte Anzeige: Zeige Lade-Platzhalter oder die Kamera-Vorschau
                            if (isCameraReady) {
                                // Wenn die Kamera bereit ist, zeige die CameraCard
                                CameraCard(
                                    modifier = Modifier
                                        .align(alignment)
                                        .padding(cardPadding)
                                        .size(width = animatedWidth, height = animatedHeight)
                                        .zIndex(1f) // Stellt sicher, dass die Karte über dem Blur-Hintergrund ist
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { isCameraMaximized = !isCameraMaximized },
                                    cameraController = cameraController
                                )
                            } else {
                                // Solange die Kamera nicht bereit ist, zeige einen Platzhalter
                                Card(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd) // Startposition
                                        .padding(20.dp)          // Start-Padding
                                        .size(width = 240.dp, height = 180.dp), // Startgröße
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }

                Taskbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) { /* Inhalt leer */ }
            }
        }
    }
}

// Der Rest des Codes (CameraCard, Taskbar) bleibt unverändert.
@Composable
private fun CameraCard(
    modifier: Modifier = Modifier,
    cameraController: MutableState<CameraController?>
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            cameraConfiguration = {
                setCameraLens(CameraLens.BACK)
                setFlashMode(FlashMode.OFF)
                setImageFormat(ImageFormat.JPEG)
                setDirectory(Directory.PICTURES)
                setQualityPrioritization(QualityPrioritization.QUALITY)
            },
            onCameraControllerReady = {
                cameraController.value = it
            },
        )
    }
}


@Composable
fun Taskbar(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card (modifier = modifier
        .animateContentSize()
        .wrapContentWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E2E2E),
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = { /* TODO: Menü-Klick-Logik hier einfügen */ }) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menü",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            Spacer(Modifier.width(4.dp))
            content()
        }
    }
}