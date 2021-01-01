package com.lhwdev.compose.material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


private val TopEffectHeightAmbient = ambientOf { 0.dp }

@Composable
val topEffectHeight
	get() = TopEffectHeightAmbient.current


@Composable
fun TopEffectorOverlay(
	topEffect: @Composable () -> Unit,
	bodyContent: @Composable (effectHeight: Dp) -> Unit
) {
	SubcomposeLayout { constraints ->
		val layoutWidth = constraints.maxWidth
		val layoutHeight = constraints.maxHeight
		
		val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
		
		layout(layoutWidth, layoutHeight) {
			val topEffectPlaceables = subcompose(true, topEffect).map {
				it.measure(looseConstraints)
			}
			
			val topEffectHeight = topEffectPlaceables.maxByOrNull { it.height }?.height ?: 0
			
			val bodyContentPlaceables = subcompose(false) {
				with(AmbientDensity.current) {
					val height = topEffectHeight.toDp()
					Providers(TopEffectHeightAmbient provides height) {
						bodyContent(height)
					}
				}
			}.map {
				it.measure(looseConstraints)
			}
			
			bodyContentPlaceables.forEach {
				it.place(0, 0)
			}
			
			topEffectPlaceables.forEach {
				it.place(0, 0)
			}
		}
	}
}
