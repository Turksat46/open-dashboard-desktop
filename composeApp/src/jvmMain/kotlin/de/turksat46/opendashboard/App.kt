package de.turksat46.opendashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import opendashboard.composeapp.generated.resources.Res
import opendashboard.composeapp.generated.resources.baseline_pause_24
import opendashboard.composeapp.generated.resources.baseline_skip_next_24
import opendashboard.composeapp.generated.resources.baseline_skip_previous_24
import opendashboard.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.skia.Bitmap
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(true) }
        Column(
            modifier = Modifier
                .background(color = Color(0xFF121212))
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
           Card(modifier = Modifier.padding(16.dp).fillMaxWidth(0.9f).height(40.dp).safeContentPadding(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E))) {
               Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                   Text("open::Dashboard", textAlign = TextAlign.Center, fontSize = 30.sp, color = Color.White)
               }
           }
            if(!showContent) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Bitte verbinden Sie Ihr Smartphone mit der open::Dashboard-App", textAlign = TextAlign.Center, fontSize = 30.sp, color = Color.White)
                }
            }else{
                Surface(color = Color.Transparent,modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxHeight(), horizontalArrangement = Arrangement.Start){

                        InfoAndControlsOverlay(
                            20.0f,
                            onSettingsToggle = {},
                            showSettingsButton = true,
                        )

                        InfoPanelMedia(modifier = Modifier.padding(16.dp).weight(0.8f))

                    }
                }
            }
        }
    }
}

@Composable
fun InfoAndControlsOverlay(
    speed: Float,
    onSettingsToggle: () -> Unit,
    showSettingsButton: Boolean,
) {
    Box(modifier = Modifier.wrapContentSize()) {
        
        val speedSignsPadding = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp) // Added end padding for TopCenter


        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .then(speedSignsPadding),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    val speedKmh = (speed * 3.6f).roundToInt()
                    Text(
                        text = "$speedKmh",
                        color = Color.White,
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "km/h", color = Color.LightGray, fontSize =  16.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        AnimatedVisibility(showSettingsButton, enter = fadeIn(tween(300)), exit = fadeOut(tween(300)), modifier = Modifier.align(Alignment.TopEnd)) {
            Column(modifier = Modifier.align(Alignment.TopEnd)){
                IconButton(
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .size(60.dp),
                    onClick = onSettingsToggle
                ) {
//                    Icon(
//                        Icons.Filled.Settings,
//                        contentDescription = "Einstellungen",
//                        tint = Color.White,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
//                            .padding(12.dp)
//                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {}


    }
}

// InfoPanelMedia Composable
@Composable
fun InfoPanelMedia(modifier: Modifier = Modifier) {

        Card(
            modifier = modifier.fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF121212),
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) { // Changed from fillMaxWidth(0.5f)
                Image(
                    painterResource(Res.drawable.compose_multiplatform),
                    contentDescription = "Album Art Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(radius = 3.dp) // Slight blur for background
                        .animateContentSize(),
                    alpha = 0.9f // Slightly transparent
                )
                Box( // Darkening overlay for better text contrast
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
            }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    val titleTextColor = Color.White
                    val bodyTextColor =  Color.LightGray.copy(alpha = 0.9f)
                    val labelTextColor = titleTextColor.copy(alpha = 0.7f)

                    val textInfoBackgroundColor = Color.Black.copy(alpha = 0.4f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(
                                textInfoBackgroundColor.copy(alpha = 0.8f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Spacer(Modifier.height(4.dp))

                        Text(
                            text =  "Keine Wiedergabe",
                            style = MaterialTheme.typography.titleLarge,
                            color = titleTextColor,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )

                            Text(
                                text = "Test",
                                style = MaterialTheme.typography.bodyLarge,
                                color = bodyTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                            )
                        }

                            Text(
                                text = "via Spotify", // You might want to map package name to app name
                                style = MaterialTheme.typography.labelMedium,
                                color = labelTextColor,
                                textAlign = TextAlign.Center,
                            )

                    }

                        val controlBgColor = Color.Black.copy(alpha = 0.55f)

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 0.dp)){
                            MediaControls(
                                isPlaying = true,
                                backgroundColor = controlBgColor,
                                onPlay = {  },
                                onPause = {  },
                                onSkipNext = {  },
                                onSkipPrevious = {  },
                                canControl = true
                            )
                            Spacer(Modifier.height(8.dp))
                        }
        }
    }

// MediaControls Composable - Modified to accept lambdas and control flag
@Composable
fun MediaControls(
    isPlaying: Boolean,
    backgroundColor: Color,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    canControl: Boolean,

    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(28.dp)), // More rounded
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSkipPrevious, enabled = canControl) {
            Icon(
                painterResource(Res.drawable.baseline_skip_previous_24),
                "Vorheriger",
                //tint = iconTintColor,
                modifier = Modifier.size(30.dp) // Slightly smaller
            )
        }
        IconButton(
            onClick = if (isPlaying) onPause else onPlay,
            enabled = canControl,
            modifier = Modifier
                .size(60.dp) // Larger central button
                .background(
                     Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            //imageVector = if (isPlaying) ImageVector.vectorResource(R.drawable.baseline_pause_24) else Icons.Default.PlayArrow,

            Icon(
                painterResource(Res.drawable.baseline_pause_24),
                contentDescription = if (isPlaying) "Pause" else "Play",
                //tint = iconTintColor,
                modifier = Modifier.size(if (isPlaying) 30.dp else 36.dp) // Pause icon can be smaller
            )
        }
        IconButton(onClick = onSkipNext, enabled = canControl) {
            Icon(
                painterResource(Res.drawable.baseline_skip_next_24),
                "Nächster",
                //tint = iconTintColor,
                modifier = Modifier.size(30.dp) // Slightly smaller
            )
        }
    }
}
