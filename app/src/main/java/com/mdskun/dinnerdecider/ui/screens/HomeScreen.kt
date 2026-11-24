package com.mdskun.dinnerdecider.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdskun.dinnerdecider.R
import com.mdskun.dinnerdecider.ui.theme.*
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel

@Composable
fun HomeScreen(
    viewModel: DinnerViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🍽️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 26.sp,
                fontWeight = FontWeight.W900,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 31.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.app_tagline),
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MenuCard(
                emoji = "📋",
                title = stringResource(R.string.nav_dinner_list),
                subtitle = stringResource(R.string.meals_saved, viewModel.dinners.size),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.navigateTo("list") }
            )
            MenuCard(
                emoji = "🎡",
                title = stringResource(R.string.nav_spin_wheel),
                subtitle = stringResource(R.string.nav_spin_subtitle),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = { viewModel.navigateTo("spin") }
            )
            MenuCard(
                emoji = "⚙️",
                title = stringResource(R.string.nav_settings),
                subtitle = stringResource(R.string.nav_settings_subtitle, viewModel.notifTime),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                accentColor = MaterialTheme.colorScheme.tertiary,
                onClick = { viewModel.navigateTo("settings") }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (viewModel.notifEnabled) stringResource(R.string.notification_on_at, viewModel.notifTime)
            else stringResource(R.string.notification_off),
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 28.dp)
        )
    }
}

@Composable
fun MenuCard(
    emoji: String,
    title: String,
    subtitle: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .border(2.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 26.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.W800,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(text = "›", fontSize = 18.sp, color = accentColor)
    }
}