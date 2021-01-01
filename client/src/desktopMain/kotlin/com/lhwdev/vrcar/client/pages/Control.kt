package com.lhwdev.vrcar.client.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLifecycleObserver
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lhwdev.compose.lazyEffect
import com.lhwdev.vrcar.Controller
import com.lhwdev.vrcar.client.AppRouteId
import com.lhwdev.vrcar.client.AppRouteInfo
import com.lhwdev.vrcar.client.AppUiState
import com.lhwdev.vrcar.client.CarIdInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


val ControlRoute = AppRouteId<CarIdInfo>(info = {
	AppRouteInfo(title = "Connected to Car@${it.host}:${it.port}", appUiState = AppUiState.noAppBar)
}) {
	Control(it)
}


sealed class ControlTarget {
	object Uninitialized : ControlTarget()
	object Error : ControlTarget()
	data class Backend(val to: Controller) : ControlTarget(), CompositionLifecycleObserver {
		override fun onLeave() {
			to.close()
		}
	}
}


@Composable
fun Control(info: CarIdInfo) {
	val controller = lazyEffect(info, initial = { ControlTarget.Uninitialized }, onError = { ControlTarget.Error }) {
		withContext(Dispatchers.IO) { ControlTarget.Backend(Controller(info.host, info.port)) }
	}
	
	when(controller) {
		ControlTarget.Uninitialized -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text("Loading", style = MaterialTheme.typography.h3)
		}
		ControlTarget.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text("Error", style = MaterialTheme.typography.h3)
		}
		is ControlTarget.Backend -> ControllerComponent(controller.to)
	}
}


@Composable
fun ControllerComponent(controller: Controller) {
	val scope = rememberCoroutineScope()
	scope.launch { controller.connect() }
}
