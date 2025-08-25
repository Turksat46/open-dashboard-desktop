package de.turksat46.opendashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import opendashboard.composeapp.generated.resources.Res
import opendashboard.composeapp.generated.resources.compose_multiplatform
import java.awt.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(color = androidx.compose.ui.graphics.Color(0xFF121212))
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
           Card(modifier = Modifier.padding(16.dp).fillMaxWidth(0.9f).height(40.dp).safeContentPadding(), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFF2E2E2E))) {
               Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                   Text("open::Dashboard", textAlign = TextAlign.Center, fontSize = 30.sp, color = androidx.compose.ui.graphics.Color.White)
               }
           }
            if(!showContent) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Bitte verbinden Sie Ihr Smartphone mit der open::Dashboard-App", textAlign = TextAlign.Center, fontSize = 30.sp, color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }
}