package com.lhwdev.compose.ui.desktop

import androidx.compose.desktop.AppFrame
import androidx.compose.desktop.AppManager
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.emptyContent
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import java.awt.MouseInfo


@Composable
fun WindowDraggableArea(
	modifier: Modifier = Modifier,
	children: @Composable () -> Unit = emptyContent()
) {
	Box(
		modifier = modifier.dragGestureFilterNoOverride(dragObserver = remember { DragHandler() })
	) {
		children()
	}
}

private class DragHandler : DragObserver {
	private var location = Offset.Zero
	private var cursor = Offset.Zero
	private lateinit var window: AppFrame
	
	override fun onStart(downPosition: Offset) {
		if(!::window.isInitialized) {
			window = AppManager.focusedWindow!!
		}
		location = Offset(
			window.x.toFloat(),
			window.y.toFloat()
		)
		val point = MouseInfo.getPointerInfo().location
		cursor = Offset(
			point.x.toFloat(),
			point.y.toFloat()
		)
	}
	
	override fun onStop(velocity: Offset) {
		location = Offset.Zero
	}
	
	override fun onCancel() {
		location = Offset.Zero
	}
	
	override fun onDrag(dragDistance: Offset): Offset {
		val point = MouseInfo.getPointerInfo().location
		
		window.setLocation(
			(location.x - (cursor.x - point.x)).toInt(),
			(location.y - (cursor.y - point.y)).toInt()
		)
		
		return dragDistance
	}
}
