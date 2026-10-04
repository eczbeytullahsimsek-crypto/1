package com.meva.notes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
internal fun MevaMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val sx = size.width / 120f
        val sy = size.height / 120f
        val markBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF4A6B5D), Color(0xFF345044)),
            start = Offset(20f * sx, 20f * sy),
            end = Offset(100f * sx, 100f * sy)
        )
        drawRoundRect(
            color = Color(0xFFF4F6F4),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(32f * sx, 32f * sy)
        )
        drawRoundRect(
            color = Color(0xFFE2E7E2),
            topLeft = Offset(6f * sx, 6f * sy),
            size = Size(108f * sx, 108f * sy),
            cornerRadius = CornerRadius(26f * sx, 26f * sy),
            style = Stroke(width = 1.5f * sx)
        )
        val leaf = Path().apply {
            moveTo(60f * sx, 26f * sy)
            cubicTo(42f * sx, 26f * sy, 32f * sx, 39f * sy, 32f * sx, 55f * sy)
            cubicTo(32f * sx, 72f * sy, 48f * sx, 87f * sy, 60f * sx, 93f * sy)
            cubicTo(72f * sx, 87f * sy, 88f * sx, 72f * sy, 88f * sx, 55f * sy)
            cubicTo(88f * sx, 39f * sy, 78f * sx, 26f * sy, 60f * sx, 26f * sy)
            close()
        }
        drawPath(leaf, markBrush, style = Stroke(width = 4.5f * sx))
        drawLine(markBrush, Offset(60f * sx, 33f * sy), Offset(60f * sx, 84f * sy), 4f * sx)
        val upperVein = Path().apply {
            moveTo(60f * sx, 48f * sy)
            cubicTo(69f * sx, 44f * sy, 75f * sx, 39f * sy, 77f * sx, 34f * sy)
        }
        val lowerVein = Path().apply {
            moveTo(60f * sx, 62f * sy)
            cubicTo(49f * sx, 59f * sy, 43f * sx, 53f * sy, 41f * sx, 46f * sy)
        }
        drawPath(upperVein, markBrush, style = Stroke(width = 3.5f * sx))
        drawPath(lowerVein, markBrush, style = Stroke(width = 3.5f * sx))
        drawCircle(Color(0xFF4A6B5D), radius = 2.5f * sx, center = Offset(60f * sx, 27f * sy))
    }
}

@Composable
internal fun MevaBrand(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF4F6F4),
        shadowElevation = 2.dp
    ) {
        Box(Modifier.padding(2.dp)) {
            MevaMark(Modifier.fillMaxSize())
        }
    }
}
