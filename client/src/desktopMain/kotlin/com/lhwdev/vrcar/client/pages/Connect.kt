package com.lhwdev.vrcar.client.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.lhwdev.compose.material.topEffectHeight
import com.lhwdev.compose.navigation.navigationState
import com.lhwdev.compose.navigation.replaceRoute
import com.lhwdev.compose.scaffoldState
import com.lhwdev.compose.showSnackbar
import com.lhwdev.compose.ui.AlteredSurface
import com.lhwdev.vrcar.client.*
import kotlinx.coroutines.launch


val ConnectRoute = AppRouteId<Unit>(
	info = AppRouteInfo(
		title = AppInfo.title,
		appUiState = AppUiState.noAppBar,
		windowHint = WindowHint(size = IntSize(400, 620))
	),
	name = "Home"
) {
	ConnectMain()
}


@Composable
fun ConnectMain() {
	AlteredSurface(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier.padding(16.dp, 128.dp + topEffectHeight, 16.dp, 64.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			val scope = rememberCoroutineScope()
			val scaffoldState = scaffoldState
			val navigationState = navigationState
			
			
			Text("Connect to the Car", style = MaterialTheme.typography.h4)
			var text by remember { mutableStateOf("") }
			
			Spacer(Modifier.weight(1f))
			
			val submit: () -> Unit = {
				val info = CarIdInfo(text)
				if(info == null) scope.launch {
					scaffoldState.showSnackbar("Wrong host address", "Okay")
				}
				else navigationState.replaceRoute(ControlRoute(info))
			}
			
			TextField(
				text,
				onValueChange = { text = it },
				label = { Text("Host address") },
				onImeActionPerformed = { _, _ -> submit() },
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text)
			)
			
			Spacer(Modifier.height(16.dp))
			
			TextButton(onClick = submit) {
				Text("Okay")
			}
		}
	}
}
