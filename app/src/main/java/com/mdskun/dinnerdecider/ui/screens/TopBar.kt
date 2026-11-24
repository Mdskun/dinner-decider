package com.mdskun.dinnerdecider.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdskun.dinnerdecider.ui.theme.*

@Composable
fun TopBar(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .border(1.dp, CardBorder)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBack, contentPadding = PaddingValues(4.dp)) {
            Text(text = "‹", fontSize = 22.sp, color = AccentColor)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.W800,
            color = DarkText
        )

        if (subtitle != null) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.W700,
                color = SubtitleText
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}