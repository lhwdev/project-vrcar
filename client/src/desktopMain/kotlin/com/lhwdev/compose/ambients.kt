package com.lhwdev.compose

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf


val AmbientScaffoldState = ambientOf<ScaffoldState>()

@Composable
val scaffoldState
	get() = AmbientScaffoldState.current

@OptIn(ExperimentalMaterialApi::class)
suspend fun ScaffoldState.showSnackbar(
	message: String,
	actionLabel: String? = null,
	duration: SnackbarDuration = SnackbarDuration.Short
) {
	snackbarHostState.showSnackbar(message, actionLabel, duration)
}
