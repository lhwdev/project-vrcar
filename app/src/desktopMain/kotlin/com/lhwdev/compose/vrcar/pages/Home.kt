package com.lhwdev.compose.vrcar.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.lhwdev.compose.material.topEffectHeight
import com.lhwdev.compose.ui.AlteredSurface
import com.lhwdev.compose.vrcar.*


val HomeRoute = AppRouteId<Unit>(
	info = AppRouteInfo(
		title = AppInfo.title, isRoot = true,
		appUiState = AppUiState.noAppBar,
		windowHint = WindowHint(size = IntSize(400, 620))
	),
	name = "Home"
) {
	Home()
}


@Composable
fun Home() {
	AlteredSurface(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier.padding(16.dp, 128.dp + topEffectHeight, 16.dp, 64.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text("Connect to the Car", style = MaterialTheme.typography.h4)
			
			Spacer(Modifier.weight(1f))
			
			var text by remember { mutableStateOf("") }
			TextField(text, onValueChange = { text = it }, label = { Text("IP address") })
			
			Spacer(Modifier.height(16.dp))
			
			TextButton(onClick = {}) {
				Text("Okay")
			}
		}
	}
}
