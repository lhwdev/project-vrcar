package com.lhwdev.vrcar.client

import com.lhwdev.vrcar.Controller
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp


sealed class Screen {
	object Connect : Screen()
	data class Control(val controller: Controller) : Screen()
}


@Stable
data class ScreenState(var screen: MutableState<Screen>)

val AmbientScreen = ambientOf<ScreenState>()


@Composable
fun App() {
	val screenState = remember { ScreenState(mutableStateOf(Screen.Connect)) }
	
	Providers(AmbientScreen provides screenState) {
		AppTheme {
			when(val screen = screenState.screen.value) {
				Screen.Connect -> Connect()
				is Screen.Control -> Control(screen.controller)
			}
		}
	}
}


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
			surface = Color.White
		),
		content = content
	)
}

fun Color.darken(fraction: Float) = lerp(this, Color.Black, fraction)
