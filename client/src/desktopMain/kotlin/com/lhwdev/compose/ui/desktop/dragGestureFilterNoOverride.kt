package com.lhwdev.compose.ui.desktop

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.*
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.debugInspectorInfo


fun Modifier.dragGestureFilterNoOverride(
	dragObserver: DragObserver,
	canDrag: ((Direction) -> Boolean)? = null
): Modifier = composed(
	inspectorInfo = debugInspectorInfo {
		name = "dragGestureFilterNoOverride"
		properties["dragObserver"] = dragObserver
		properties["canDrag"] = canDrag
	}
) {
	val glue = remember { TouchSlopDragGestureDetectorGlue() }
	glue.touchSlopDragObserver = dragObserver
	
	// TODO(b/146427920): There is a gap here where RawPressStartGestureDetector can cause a call to
	//  DragObserver.onStart but if the pointer doesn't move and releases, (or if cancel is called)
	//  The appropriate callbacks to DragObserver will not be called.
	rawDragGestureFilter(glue.rawDragObserver, glue::enabledOrStarted)
		.dragSlopExceededGestureFilter(glue::enableDrag, canDrag)
		.rawPressStartGestureFilter(
			glue::startDrag,
			true,
			PointerEventPass.Main
		)
	
}

/**
 * Glues together the logic of RawDragGestureDetector, TouchSlopExceededGestureDetector, and
 * InterruptFlingGestureDetector.
 */
private class TouchSlopDragGestureDetectorGlue {
	lateinit var touchSlopDragObserver: DragObserver
	var started = false
	var enabled = false
	val enabledOrStarted
		get() = started || enabled
	
	fun enableDrag() {
		enabled = true
	}
	
	fun startDrag(downPosition: Offset) {
		started = true
		touchSlopDragObserver.onStart(downPosition)
	}
	
	val rawDragObserver: DragObserver =
		object : DragObserver {
			override fun onStart(downPosition: Offset) {
				if (!started) {
					touchSlopDragObserver.onStart(downPosition)
				}
			}
			
			override fun onDrag(dragDistance: Offset): Offset {
				return touchSlopDragObserver.onDrag(dragDistance)
			}
			
			override fun onStop(velocity: Offset) {
				started = false
				enabled = false
				touchSlopDragObserver.onStop(velocity)
			}
			
			override fun onCancel() {
				started = false
				enabled = false
				touchSlopDragObserver.onCancel()
			}
		}
}
