package com.example.trabalhosma.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

val Teal200 = Color(0xFF03DAC5)
val Teal700 = Color(0xFF018786)


val LightColorScheme = lightColorScheme(
    primary = Teal700,
    secondary = Teal200,
    onPrimary = Color.White,
    onSecondary = Color.Black
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

val Shapes = Shapes(
    small = RoundedCornerShape(1.dp),
    medium = RoundedCornerShape(1.dp),
    large = RoundedCornerShape(1.dp)
)