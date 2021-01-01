package com.lhwdev.vrcar.client

import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.runtime.Composable


@Composable
actual fun provideWindowHint(hint: WindowHint) {
	val window = AppWindowAmbient.current
	hint.size?.let { (width, height) -> window?.setSize(width, height) }
}
