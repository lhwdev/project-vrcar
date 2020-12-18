package com.lhwdev.compose.vrcar

import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.runtime.Composable


@Composable
actual fun provideWindowHint(hint: WindowHint) {
	val window = AppWindowAmbient.current
	hint.size?.let { (width, height) -> window?.setSize(width, height) }
}
