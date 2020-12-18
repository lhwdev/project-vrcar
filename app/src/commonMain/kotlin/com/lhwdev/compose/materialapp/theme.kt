package com.lhwdev.compose.materialapp

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp


@Composable
fun AppTheme(content: @Composable () -> Unit) {
	val primary = Color(0xFF2962FF)
	val secondary = Color(0xFFFFD40)
	
	MaterialTheme(
		colors = lightColors(
			primary = primary,
			primaryVariant = primary.darken(0.1f),
			secondary = secondary,
			secondaryVariant = secondary.darken(0.1f),
			background = Color(0xfff5f5f5),
			surface = Color.White,
		),
		content = content
	)
}

fun Color.darken(fraction: Float) = lerp(this, Color.Black, fraction)
