package com.lhwdev.compose.vrcar.desktop

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.WindowEvents
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.ExperimentalKeyInput
import androidx.compose.ui.unit.IntSize
import com.lhwdev.compose.AppWindowFrame
import com.lhwdev.compose.IsWindowDocked
import com.lhwdev.compose.vrcar.AppTheme
import javax.swing.JFrame
import javax.swing.SwingUtilities.invokeLater


@OptIn(ExperimentalKeyInput::class)
fun main() = invokeLater {
	var isDockedState by mutableStateOf(false)
	
	lateinit var window: AppWindow
	window = AppWindow(
		title = "Jetpack Compose test",
		size = IntSize(720, 480),
		undecorated = true,
		events = WindowEvents(onResize = {
			val isDocked = window.window.extendedState and JFrame.MAXIMIZED_BOTH == 0
			if(isDockedState != isDocked) isDockedState = isDocked
		})
	)
	
	window.window.isResizable = true

//	window.window.contentPane.background = java.awt.Color(255, 255, 255, 0)
//	window.window.background = java.awt.Color(0, 0, 0, 0)
	window.show {
		Providers(
			IsWindowDocked provides isDockedState
		) {
			DesktopTheme {
				AppTheme {
					AppWindowFrame()
				}
			}
		}
	}
}
