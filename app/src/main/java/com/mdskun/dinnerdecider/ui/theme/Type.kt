package com.mdskun.dinnerdecider.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = androidx.compose.material3.Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.W900,
        fontSize = 26.sp,
        lineHeight = 31.sp,
        color = DarkText
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W800,
        fontSize = 22.sp,
        lineHeight = 27.sp,
        color = DarkText
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.W800,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = DarkText
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.W800,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        color = DarkText
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.W800,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        color = DarkText
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        color = DarkText
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        color = SubtitleText
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = SubtitleText
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = SubtitleText
    )
)