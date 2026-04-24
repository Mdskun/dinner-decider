package com.mdskun.dinnerdecider.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdskun.dinnerdecider.ui.theme.*
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight


@Composable
fun SpinScreen(
    viewModel: DinnerViewModel,
    modifier: Modifier = Modifier
) {
    var currentAngle by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    // Function to spin the wheel
    fun performSpin() {
        if (!viewModel.spinning && viewModel.dinners.isNotEmpty()) {
            viewModel.startSpin()
            viewModel.displayResult(false)
            viewModel.setSpinResult(null)

            val extraSpins = (5f + Math.random().toFloat() * 5f) * 2f * PI.toFloat()
            val targetAngle = currentAngle + extraSpins
            val spinDuration = 3500L

            kotlinx.coroutines.MainScope().launch {
                val steps = 60
                val stepDuration = spinDuration / steps
                val startAngle = currentAngle

                for (i in 1..steps) {
                    val progress = i.toFloat() / steps
                    val eased = 1f - (1f - progress).pow(4)
                    currentAngle = startAngle + extraSpins * eased
                    delay(stepDuration)
                }

                currentAngle = targetAngle

                // Calculate result
                val normalized = (((-targetAngle - PI.toFloat() / 2f) % (2f * PI.toFloat())) + 2f * PI.toFloat()) % (2f * PI.toFloat())
                val sliceAngle = (2f * PI.toFloat() / viewModel.dinners.size)
                val index = (normalized / sliceAngle).toInt() % viewModel.dinners.size

                viewModel.setSpinResult(viewModel.dinners[index])
                viewModel.stopSpin()
                delay(100)
                viewModel.displayResult(true)
            }
        }
    }

    Column(
        modifier = modifier.background(WarmBackground)
    ) {
        // Top Bar
        TopBar(
            title = "Spin the Wheel",
            onBack = {
                viewModel.goBack()
                currentAngle = 0f
            }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pointer
            Text(
                text = "▼",
                fontSize = 28.sp,
                color = DarkText,
                modifier = Modifier.offset(y = (-6).dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Wheel Canvas
            Canvas(
                modifier = Modifier
                    .size(310.dp)
                    .clip(CircleShape)
                    .shadow(18.dp, CircleShape)
                    .background(White, CircleShape)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val cx = canvasWidth / 2
                val cy = canvasHeight / 2
                val radius = cx - 8.dp.toPx()
                val sliceAngle = (2 * PI / viewModel.dinners.size).toFloat()

                // Calculate text size as plain float first
                val calculatedTextSize = 120f / viewModel.dinners.size + 4f
                val finalTextSize = minOf(13f, calculatedTextSize) * density.density

                viewModel.dinners.forEachIndexed { index, label ->
                    val startAngle = currentAngle + index * sliceAngle
                    val sweepAngle = sliceAngle

                    // Draw slice
                    drawArc(
                        color = WheelColors[index % WheelColors.size],
                        startAngle = Math.toDegrees(startAngle.toDouble()).toFloat(),
                        sweepAngle = Math.toDegrees(sweepAngle.toDouble()).toFloat(),
                        useCenter = true,
                        topLeft = Offset(cx - radius, cy - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // Draw border
                    drawArc(
                        color = White,
                        startAngle = Math.toDegrees(startAngle.toDouble()).toFloat(),
                        sweepAngle = Math.toDegrees(sweepAngle.toDouble()).toFloat(),
                        useCenter = true,
                        topLeft = Offset(cx - radius, cy - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw text
                    val textAngle = startAngle + sliceAngle / 2
                    val textRadius = radius - 20.dp.toPx()
                    val textX = cx + textRadius * cos(textAngle)
                    val textY = cy + textRadius * sin(textAngle)

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = finalTextSize  // Use calculated float value
                            textAlign = Paint.Align.CENTER
                            typeface = Typeface.DEFAULT_BOLD
                            setShadowLayer(3f, 0f, 0f, android.graphics.Color.argb(76, 0, 0, 0))
                        }

                        val displayText = if (label.length > 14) label.take(12) + "…" else label

                        save()
                        rotate(
                            Math.toDegrees(textAngle.toDouble()).toFloat(),
                            cx,
                            cy
                        )
                        drawText(
                            displayText,
                            cx + textRadius,
                            cy,
                            paint
                        )
                        restore()
                    }
                }

                // Center dot
                drawCircle(
                    color = White,
                    radius = 14.dp.toPx(),
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = AccentColor,
                    radius = 14.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Spin Button
            Button(
                onClick = { performSpin() },
                enabled = !viewModel.spinning && viewModel.dinners.isNotEmpty(),
                modifier = Modifier
                    .shadow(
                        if (!viewModel.spinning) 20.dp else 0.dp,
                        RoundedCornerShape(18.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.spinning) CardBorder else AccentColor,
                    contentColor = White,
                    disabledContainerColor = CardBorder,
                    disabledContentColor = LightSubtitle
                ),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)
            ) {
                Text(
                    text = if (viewModel.spinning) "Spinning…" else "🎡 Spin!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W900,
                    letterSpacing = 0.5.sp
                )
            }

            // Result
            if (viewModel.showResult && viewModel.result != null) {
                Card(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, CardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tonight's dinner is…",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W700,
                            color = SubtitleText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.result!!,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W900,
                            color = AccentColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🍽️",
                            fontSize = 20.sp
                        )
                    }
                }
            }

            if (viewModel.dinners.isEmpty()) {
                Text(
                    text = "Add dinners to your list first!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    color = LightSubtitle,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    }
}