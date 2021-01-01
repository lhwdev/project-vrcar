package com.lhwdev.compose.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lhwdev.vrcar.client.darken


@Composable
fun AlteredSurface(
	color: Color = MaterialTheme.colors.primarySurface,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit
) {
	if(color == MaterialTheme.colors.surface)
		Surface(color = color, contentColor = contentColorFor(color), modifier = modifier, content = content)
	else {
		val onColor = contentColorFor(color)
		MaterialTheme(MaterialTheme.colors.let {
			it.copy(
				primary = it.surface,
				primaryVariant = it.surface.darken(0.05f),
				onPrimary = it.onSurface,
				surface = color,
				onSurface = onColor
			)
		}, MaterialTheme.typography, MaterialTheme.shapes) {
			ProvideTextStyle(AmbientTextStyle.current.copy(color = onColor)) {
				Surface(color = color, contentColor = onColor, modifier = modifier, content = content)
			}
		}
	}
}
