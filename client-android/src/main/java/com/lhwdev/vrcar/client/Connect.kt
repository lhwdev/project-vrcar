package com.lhwdev.vrcar.client

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.lhwdev.vrcar.Controller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun Connect() {
	val screen = AmbientScreen.current
	val scope = rememberCoroutineScope()
	
	Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
		Text("Connect to car", style = MaterialTheme.typography.h3)
		
		val (text, setText) = remember { mutableStateOf(TextFieldValue()) }
		TextField(text, setText, modifier = Modifier.fillMaxWidth())
		TextButton(onClick = {
			scope.launch {
				screen.screen.value = withContext(Dispatchers.IO) { // because android.os.NetworkOnMainThreadException
					Screen.Control(Controller(text.text, 2536, 8000))
				}
			}
		}) {
			Text("Okay")
		}
	}
}
