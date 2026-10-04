package com.meva.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun WelcomeScreen(
    onStart: () -> Unit,
    onQuickNote: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MevaPalette.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .size(340.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x66C6EBD9), Color.Transparent),
                        radius = 540f
                    ),
                    CircleShape
                )
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(370.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x44FFDBD0), Color.Transparent),
                        radius = 560f
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 390.dp)
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(132.dp),
                shape = RoundedCornerShape(32.dp),
                color = MevaPalette.surfaceLow,
                shadowElevation = 12.dp
            ) {
                Box(Modifier.padding(18.dp), contentAlignment = Alignment.Center) {
                    MevaMark(Modifier.fillMaxSize())
                }
            }

            Spacer(Modifier.height(30.dp))
            Text(
                text = "Meva Notes",
                fontSize = 34.sp,
                lineHeight = 42.sp,
                letterSpacing = (-0.7).sp,
                fontWeight = FontWeight.SemiBold,
                color = MevaPalette.onSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Zihnine alan aç.",
                fontSize = 15.sp,
                letterSpacing = 0.2.sp,
                color = MevaPalette.onSurfaceVariant
            )

            Spacer(Modifier.height(42.dp))
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MevaPalette.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Başla", fontWeight = FontWeight.Medium, letterSpacing = 0.3.sp)
                Spacer(Modifier.size(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }

            Button(
                onClick = onQuickNote,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 4.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MevaPalette.tertiary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Yeni Not Oluştur", fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
            }

            Spacer(Modifier.height(28.dp))
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MevaPalette.surface.copy(alpha = 0.86f))
                    .padding(horizontal = 15.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(MevaPalette.primary)
                )
                Text(
                    text = "v1.0  •  Güvenli & Yerel Depolama",
                    color = MevaPalette.outline,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.25.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
