package com.lhwdev.vrcar.client

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.pressIndicatorGestureFilter


fun Modifier.pressIndicatorGestureFilter(
	onDown: ((Offset) -> Unit)? = null,
	onUp: (() -> Unit)? = null
) = pressIndicatorGestureFilter(onStart = onDown, onStop = onUp, onCancel = onUp)
