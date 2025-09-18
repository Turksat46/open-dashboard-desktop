package de.turksat46.opendashboard.modules

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Taskbar(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
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