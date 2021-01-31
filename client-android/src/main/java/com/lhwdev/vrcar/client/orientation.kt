package com.lhwdev.vrcar.client

import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit


@Composable
fun FixOrientation() {
	val activity = AmbientActivity.current
	
	onCommit {
		activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
	}
}
