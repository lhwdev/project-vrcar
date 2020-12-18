package com.lhwdev.compose.material

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy


@Composable
fun TopEffectorOverlay(
	topEffect: @Composable () -> Unit,
	bodyContent: @Composable (effectHeight: Dp) -> Unit
) {
	SubcomposeLayout<Boolean> { constraints ->
		val layoutWidth = constraints.maxWidth
		val layoutHeight = constraints.maxHeight
		
		val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
		
		layout(layoutWidth, layoutHeight) {
			val topEffectPlaceables = subcompose(true, topEffect).fastMap {
				it.measure(looseConstraints)
			}
			
			val topEffectHeight = topEffectPlaceables.fastMaxBy { it.height }?.height ?: 0
			
			val bodyContentPlaceables = subcompose(false) {
				with(DensityAmbient.current) { bodyContent(topEffectHeight.toDp()) }
			}.fastMap {
				it.measure(looseConstraints)
			}
			
			bodyContentPlaceables.fastForEach {
				it.place(0, 0)
			}
			
			topEffectPlaceables.fastForEach {
				it.place(0, 0)
			}
		}
	}
}
