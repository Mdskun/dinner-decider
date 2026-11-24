package com.mdskun.dinnerdecider.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdskun.dinnerdecider.R
import com.mdskun.dinnerdecider.ui.component.SpinWheel
import com.mdskun.dinnerdecider.ui.component.TopBar
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.random.Random

@Composable
fun SpinScreen(
    viewModel: DinnerViewModel,
    modifier: Modifier = Modifier
) {
    val rotationAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun performSpin() {
        val dinners = viewModel.dinners
        if (viewModel.spinning || dinners.isEmpty()) return

        val selectedDinner = viewModel.pickDinnerForSpin() ?: return
        val winnerIndex = dinners.indexOf(selectedDinner).let { if (it >= 0) it else 0 }

        val totalSlices = dinners.size
        val sliceAngle = (2f * PI.toFloat() / totalSlices)

        // Pointing top center (1.5 * PI)
        val sliceCenterOffset = winnerIndex * sliceAngle + (sliceAngle / 2f)
        val targetModulo = (1.5f * PI.toFloat() - sliceCenterOffset) % (2f * PI.toFloat())

        val currentVal = rotationAnim.value
        val currentMod = (currentVal % (2f * PI.toFloat()) + 2f * PI.toFloat()) % (2f * PI.toFloat())
        val deltaMod = ((targetModulo - currentMod) % (2f * PI.toFloat()) + 2f * PI.toFloat()) % (2f * PI.toFloat())

        val extraSpins = (6 + Random.nextInt(4)) * 2f * PI.toFloat()
        val targetRotation = currentVal + extraSpins + deltaMod

        scope.launch {
            viewModel.startSpin()
            viewModel.displayResult(false)
            viewModel.setSpinResult(null)

            rotationAnim.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(
                    durationMillis = 3500,
                    easing = CubicBezierEasing(0.15f, 0.85f, 0.25f, 1.0f)
                )
            )

            viewModel.setSpinResult(selectedDinner)
            viewModel.stopSpin()
            viewModel.displayResult(true)
        }
    }

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(
            title = stringResource(R.string.spin_wheel_title),
            onBack = {
                viewModel.goBack()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Pointer indicator
            Text(
                text = "▼",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.offset(y = (-4).dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Spin Wheel Component
            SpinWheel(
                dinners = viewModel.dinners,
                currentAngle = rotationAnim.value
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Spin Button
            Button(
                onClick = { performSpin() },
                enabled = !viewModel.spinning && viewModel.dinners.isNotEmpty(),
                modifier = Modifier
                    .shadow(
                        if (!viewModel.spinning) 16.dp else 0.dp,
                        RoundedCornerShape(18.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.spinning) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.outlineVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)
            ) {
                Text(
                    text = if (viewModel.spinning) stringResource(R.string.spinning) else stringResource(R.string.spin_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W900,
                    letterSpacing = 0.5.sp
                )
            }

            // Result Display Card
            if (viewModel.showResult && viewModel.result != null) {
                Card(
                    modifier = Modifier.padding(top = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.result_header),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W700,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.result!!,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W900,
                            color = MaterialTheme.colorScheme.primary
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
                    text = stringResource(R.string.add_dinners_first),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    }
}