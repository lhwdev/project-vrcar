package com.lhwdev.vrcar.client

import androidx.compose.runtime.Composable
import com.lhwdev.compose.isChanged
import com.lhwdev.compose.ui.desktop.currentAppWindow
import com.lhwdev.compose.ui.desktop.hasResized


@Composable
actual fun ProvideWindowHint(hint: WindowHint) {
	val window = currentAppWindow
	
	if(isChanged(hint)) {
		if(!hasResized) hint.size?.let { (width, height) -> window.setSize(width, height) }
	}
}
