package com.mdskun.dinnerdecider.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdskun.dinnerdecider.ui.theme.*
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel

@Composable
fun SettingsScreen(
    viewModel: DinnerViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(WarmBackground)
    ) {
        // Top Bar
        TopBar(
            title = "Settings",
            onBack = { viewModel.goBack() }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Section Title
            Text(
                text = "NOTIFICATIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.W800,
                color = LightSubtitle,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Suggestion",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W800,
                            color = DarkText
                        )
                        Text(
                            text = "Get a random dinner pick every day",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W600,
                            color = SubtitleText
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    // Custom Toggle
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (viewModel.notifEnabled) AccentColor
                                else CardBorder
                            )
                            .clickable { viewModel.toggleNotification(!viewModel.notifEnabled) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .offset(
                                    x = if (viewModel.notifEnabled) 25.dp else 3.dp,
                                    y = 3.dp
                                )
                                .clip(CircleShape)
                                .background(White)
                                .shadow(1.dp, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time Picker Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (viewModel.notifEnabled) White else White.copy(alpha = 0.7f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notification Time",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W800,
                            color = DarkText
                        )
                        Text(
                            text = "Pick what time to send the suggestion",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W600,
                            color = SubtitleText
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    // Time display
                    Text(
                        text = viewModel.notifTime,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W800,
                        color = if (viewModel.notifEnabled) AccentColor else LightSubtitle,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(WarmBackground)
                            .border(2.dp, CardBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About Section
            Text(
                text = "ABOUT",
                fontSize = 12.sp,
                fontWeight = FontWeight.W800,
                color = LightSubtitle,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Dinner Decider",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W800,
                        color = DarkText
                    )
                    Text(
                        text = "Made with ❤️ so mom never has to wonder again.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W600,
                        color = SubtitleText
                    )
                }
            }
        }
    }
}