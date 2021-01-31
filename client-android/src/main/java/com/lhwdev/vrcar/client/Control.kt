package com.lhwdev.vrcar.client

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.github.niqdev.mjpeg.DisplayMode
import com.github.niqdev.mjpeg.Mjpeg
import com.github.niqdev.mjpeg.MjpegSurfaceView
import com.lhwdev.vrcar.Controller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun <T> MutableStateFlow<T>.tryEmitDiffer(value: T) = if(value == this.value) true else tryEmit(value)


data class Ref<T>(var value: T) : ReadWriteProperty<Any?, T> {
	override fun getValue(thisRef: Any?, property: KProperty<*>) = value
	
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		this.value = value
	}
}


@Stable
class ControlFacade(val controller: Controller) {
	private val outputFlow = MutableStateFlow(0f to 0f)
	
	suspend fun init(scope: CoroutineScope) {
		controller.connect()
		
		scope.launch {
			outputFlow.collect { (l, r) ->
				controller.raw(l, r)
			}
		}
	}
	
	suspend fun dispose() {
		controller.close()
	}
	
	fun output(left: Float, right: Float) {
		outputFlow.tryEmitDiffer(-left to -right)
	}
}


@Composable
fun Control(controller: Controller) {
	val facade = remember { ControlFacade(controller) }
	
	LaunchedEffect(Unit) {
		facade.init(CoroutineScope(Dispatchers.IO))
	}
	
	onDispose {
		GlobalScope.launch { facade.dispose() }
	}
	
	FixOrientation()
	
	Box(Modifier.fillMaxSize()) {
		AndroidView(
			viewBlock = {
				val view = MjpegSurfaceView(it, null)
				Mjpeg.newInstance()
					.open(controller.cameraUrl, 5)
					.subscribe { inputStream ->
						view.setSource(inputStream)
						view.setDisplayMode(DisplayMode.BEST_FIT)
						view.showFps(true)
					}
				view
			}
		)
		
		Row {
			val height = getMaxLayoutSize().value.height
			println(height)
			
			var left by remember<Ref<Float>> { Ref(0f) }
			var right by remember<Ref<Float>> { Ref(0f) }
			
			fun update() {
				facade.output(left, right)
			}
			
			Box(Modifier.weight(1f).fillMaxHeight().dragValueState { _, distance ->
				left = if(height > 0) (distance.y / height * 1.5f).coerceIn(-1f, 1f)
				else 0f
				update()
			})
			
			Spacer(Modifier.weight(3f))
			
			Box(Modifier.weight(1f).fillMaxHeight().dragValueState { _, distance ->
				right = if(height > 0) (distance.y / height * 1.5f).coerceIn(-1f, 1f)
				else 0f
				update()
			})
		}
		
		// Row {
		// 	fun Modifier.moveModifier(direction: Direction?, movement: Movement) = pressIndicatorGestureFilter(
		// 		onDown = {
		// 			facade.direction(direction)
		// 			facade.move(movement)
		// 		},
		// 		onUp = {
		// 			facade.move(null)
		// 		}
		// 	)
		//
		// 	Box(
		// 		Modifier.weight(1f).fillMaxHeight().moveModifier(Direction.left, Movement.forward)
		// 	)
		// 	Column(Modifier.weight(2f)) {
		// 		Box(
		// 			Modifier.weight(2f).fillMaxWidth().moveModifier(null, Movement.forward)
		// 		)
		// 		Box(
		// 			Modifier.weight(1f).fillMaxWidth().moveModifier(null, Movement.backward)
		// 		)
		// 	}
		// 	Box(
		// 		Modifier.weight(1f).fillMaxHeight().moveModifier(Direction.right, Movement.forward)
		// 	)
		// }
		//
		// var controlVisible by remember { mutableStateOf(false) }
		//
		// if(!controlVisible) TextButton(
		// 	onClick = { controlVisible = !controlVisible },
		// 	modifier = Modifier.align(Alignment.BottomStart)
		// ) {
		// 	Text("Config")
		// }
		//
		// if(controlVisible) Column(Modifier.background(Color(0x77000000))) {
		// 	val (speed, setSpeed) = remember { mutableStateOf(facade.speed) }
		// 	Slider(speed, setSpeed)
		//
		// 	Spacer(Modifier.weight(1f))
		//
		// 	TextButton(onClick = {
		// 		controlVisible = false
		// 		facade.speed = speed
		// 	}, modifier = Modifier.fillMaxWidth().background(Color(0x55ffffff))) {
		// 		Text("Okay")
		// 	}
		// }
	}
}


fun Modifier.dragValueState(
	update: (isPressed: Boolean, distance: Offset) -> Unit
) = dragGestureFilter(object : DragObserver {
	private var original = Offset.Zero
	
	override fun onStart(downPosition: Offset) {
		original = Offset.Zero
		update(true, Offset.Zero)
	}
	
	override fun onDrag(dragDistance: Offset): Offset {
		original += dragDistance
		update(true, original)
		return dragDistance
	}
	
	override fun onCancel() {
		update(false, Offset.Zero)
	}
	
	override fun onStop(velocity: Offset) {
		update(false, Offset.Zero)
	}
})

@Composable
fun getMaxLayoutSize(): State<IntSize> {
	val state = remember { mutableStateOf(IntSize.Zero) }
	Layout({}) { _, constraints ->
		state.value = IntSize(constraints.maxWidth, constraints.maxHeight)
		layout(0, 0) {}
	}
	return state
}
