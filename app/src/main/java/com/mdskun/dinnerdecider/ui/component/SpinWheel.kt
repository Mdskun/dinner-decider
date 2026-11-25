package com.mdskun.dinnerdecider.ui.component

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mdskun.dinnerdecider.ui.theme.WheelColors
import kotlin.math.PI

@Composable
fun SpinWheel(
    dinners: List<String>,
    currentAngle: Float,
    modifier: Modifier = Modifier,
    wheelSize: Dp = 310.dp
) {
    val density = LocalDensity.current

    // Hoist Paint instance outside Canvas to prevent allocation on every frame
    val textPaint = remember {
        Paint().apply {
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(4f, 0f, 0f, android.graphics.Color.argb(128, 0, 0, 0))
        }
    }

    Canvas(
        modifier = modifier
            .size(wheelSize)
            .shadow(18.dp, CircleShape) // Shadow BEFORE clip so shadow isn't cut off
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = cx - 8.dp.toPx()
        val totalSlices = dinners.size.coerceAtLeast(1)
        val sliceAngle = (2 * PI / totalSlices).toFloat()

        // Dynamic text size calculation based on item count
        val baseTextSizeSp = when {
            totalSlices <= 6 -> 14f
            totalSlices <= 10 -> 12f
            totalSlices <= 16 -> 10f
            totalSlices <= 30 -> 8f
            else -> 7f
        }
        val finalTextSizePx = baseTextSizeSp * density.density
        textPaint.textSize = finalTextSizePx

        // Step for drawing text on large lists (50-60+ items)
        val step = when {
            totalSlices <= 20 -> 1
            totalSlices <= 35 -> 2
            totalSlices <= 50 -> 3
            else -> 4
        }

        dinners.forEachIndexed { index, label ->
            val startAngle = currentAngle + index * sliceAngle
            val sweepAngle = sliceAngle

            val sliceColor = WheelColors[index % WheelColors.size]

            // Draw slice arc
            drawArc(
                color = sliceColor,
                startAngle = Math.toDegrees(startAngle.toDouble()).toFloat(),
                sweepAngle = Math.toDegrees(sweepAngle.toDouble()).toFloat(),
                useCenter = true,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2)
            )

            // Draw slice border divider
            drawArc(
                color = Color.White,
                startAngle = Math.toDegrees(startAngle.toDouble()).toFloat(),
                sweepAngle = Math.toDegrees(sweepAngle.toDouble()).toFloat(),
                useCenter = true,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = if (totalSlices > 30) 1.dp.toPx() else 2.dp.toPx())
            )

            // Draw text label on stepped items
            if (index % step == 0) {
                val textAngle = startAngle + sliceAngle / 2
                val textRadius = radius - 24.dp.toPx()

                // Determine contrast text color based on slice background luminance
                val luminance = (sliceColor.red * 0.299f + sliceColor.green * 0.587f + sliceColor.blue * 0.114f)
                val textColor = if (luminance > 0.65f) {
                    android.graphics.Color.parseColor("#2D1B0E")
                } else {
                    android.graphics.Color.WHITE
                }
                textPaint.color = textColor

                val maxChars = when {
                    totalSlices <= 6 -> 18
                    totalSlices <= 10 -> 14
                    totalSlices <= 20 -> 10
                    else -> 7
                }

                val displayText = if (label.length > maxChars) {
                    label.take(maxChars - 1) + "…"
                } else {
                    label
                }

                drawContext.canvas.nativeCanvas.apply {
                    save()
                    rotate(
                        Math.toDegrees(textAngle.toDouble()).toFloat(),
                        cx,
                        cy
                    )
                    drawText(
                        displayText,
                        cx + textRadius * 0.55f,
                        cy + (finalTextSizePx / 3f),
                        textPaint
                    )
                    restore()
                }
            }
        }

        // Center hub dot
        drawCircle(
            color = Color.White,
            radius = 14.dp.toPx(),
            center = Offset(cx, cy)
        )
        drawCircle(
            color = Color(0xFFE8856A),
            radius = 14.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}