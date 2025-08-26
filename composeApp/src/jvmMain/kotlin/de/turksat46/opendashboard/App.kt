package de.turksat46.opendashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import opendashboard.composeapp.generated.resources.Res
import opendashboard.composeapp.generated.resources.compose_multiplatform
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
                    InfoAndControlsOverlay(
                        0.0f,
                        onSettingsToggle = {},
                        showSettingsButton = true,
                    )
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
    Box(modifier = Modifier.fillMaxSize()) {
        
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
