package com.lhwdev.vrcar.client.pages

import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver
import com.github.sarxos.webcam.ds.ipcam.IpCamMode
import com.lhwdev.compose.utils.lazyEffect
import com.lhwdev.vrcar.Controller
import com.lhwdev.vrcar.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect


val sInit: Unit = run {
	Webcam.setDriver(IpCamDriver())
}


val ControlRoute = AppRouteId<CarIdInfo>(info = {
	AppRouteInfo(title = "Connected to Car@${it.host}:${it.port}", appUiState = AppUiState.noAppBar)
}) {
	Control(it)
}


sealed class ControlTarget {
	object Uninitialized : ControlTarget()
	data class Error(val throwable: Throwable) : ControlTarget()
	data class Backend(val to: Controller) : ControlTarget()
}

@Composable
fun Control(info: CarIdInfo) {
	sInit
	
	val controller = lazyEffect(
		info,
		initial = { ControlTarget.Uninitialized },
		onError = { ControlTarget.Error(it) }
	) {
		withContext(Dispatchers.IO) { ControlTarget.Backend(Controller(info.host, info.port, 8000)) }
	}
	
	when(controller) {
		ControlTarget.Uninitialized -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text("Loading", style = MaterialTheme.typography.h3)
		}
		is ControlTarget.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text("Error", style = MaterialTheme.typography.h3)
			Text(controller.throwable.stackTraceToString())
		}
		is ControlTarget.Backend -> ControllerComponent(controller.to)
	}
}



enum class Movement { forward, backward }
enum class Direction { left, right }


fun <T> MutableStateFlow<T>.tryEmitDiffer(value: T) = if(value == this.value) true else tryEmit(value)


@Stable
class ControlFacade(val controller: Controller) {
	var speed by mutableStateOf(0.2f)
	var steeringForward by mutableStateOf(0f)
	
	private val moveFlow = MutableStateFlow<Movement?>(null)
	private val directionFlow = MutableStateFlow<Direction?>(null)
	
	suspend fun init(scope: CoroutineScope) {
		controller.connect()
		
		scope.launch {
			moveFlow.collect {
				when(it) {
					null -> controller.stop()
					Movement.forward -> controller.speed(speed)
					Movement.backward -> controller.speed(-speed)
				}
			}
		}
		
		scope.launch {
			directionFlow.collect {
				when(it) {
					null -> controller.steer(0f, steeringForward)
					Direction.left -> controller.steer(-1f, steeringForward)
					Direction.right -> controller.steer(1f, steeringForward)
				}
			}
		}
	}
	
	suspend fun dispose() {
		controller.close()
	}
	
	fun move(newDirection: Movement?) {
		moveFlow.tryEmitDiffer(newDirection)
	}
	
	fun direction(direction: Direction?) {
		println("direction")
		directionFlow.tryEmitDiffer(direction)
	}
}


@Composable
fun ControllerComponent(controller: Controller) {
	val appWindow = AppWindowAmbient.current
	
	onCommit {
		appWindow?.makeFullscreen()
	}
	
	val facade = remember { ControlFacade(controller) }
	
	// logic
	LaunchedEffect(null) {
		facade.init(this)
	}
	
	val device = remember { IpCamDeviceRegistry.register(controller.cameraName, controller.cameraUrl, IpCamMode.PUSH) }
	
	DisposableEffect(Unit) {
		onDispose {
			IpCamDeviceRegistry.unregister(device)
			GlobalScope.launch { facade.dispose() }
		}
	}
	
	
	// ui
	
	val camera = remember {
		val camera = Webcam.getWebcams()[0]
		camera.open(true)
		camera
	}
	
	val frameRate = 30
	var ready by remember { mutableStateOf(false) }
	val invalidate = invalidate
	
	LaunchedEffect(null) {
		while(isActive && !camera.isOpen) {
			delay(50L)
		}
		ready = true
		while(isActive) {
			invalidate()
			delay(1000L / frameRate)
		}
	}
	
	if(!ready) return
	
	Box {
		Box(
			modifier = Modifier.fillMaxSize()
				.graphicsLayer()
		) {
			val drawModifier = Modifier.drawWithCache {
				onDrawBehind {
					if(!camera.isOpen) return@onDrawBehind
					
					// convert from RGB_888 to RGBA_8888
					val originalArray = camera.imageBytes.array()
					
					println(size)
					drawImage(
						convertBgr888ToRgba8888Bitmap(originalArray, camera.viewSize.width, camera.viewSize.height),
						dstSize = IntSize(size.width.toInt(), size.height.toInt())
					)
				}
			}
			
			val modifier = Modifier
				.aspectRatio(camera.viewSize.width.toFloat() / camera.viewSize.height.toFloat())
				.then(drawModifier)
			
			Box(modifier)
		}
		
		Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {
			Column(modifier = Modifier.fillMaxWidth()) {
				val textFieldState = remember { mutableStateOf(TextFieldValue()) }
				val (state, setState) = textFieldState
				
				OutlinedTextField(
					state, onValueChange = {
						val original = state.text
						val new = it.text
						
						when {
							new == "/" -> setState(it)
							original == "" -> setState(TextFieldValue())
							else -> setState(it)
						}
					},
					modifier = Modifier.align(Alignment.End)
						.width(100.dp).onControlKeyEvent(facade, textFieldState)
				)
			}
		}
	}
}

expect fun Modifier.onControlKeyEvent(facade: ControlFacade, textFieldState: MutableState<TextFieldValue>): Modifier
