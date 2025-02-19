package com.lhwdev.vrcar.client.desktop

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.WindowEvents
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import com.lhwdev.compose.ui.desktop.AppWindowFrame
import com.lhwdev.compose.ui.desktop.HasResized
import com.lhwdev.compose.ui.desktop.IsWindowDocked
import com.lhwdev.vrcar.client.AppTheme
import javax.swing.JFrame
import javax.swing.SwingUtilities.invokeLater


fun main() = invokeLater {
	var isDockedState by mutableStateOf(false)
	var hasResized by mutableStateOf(false)
	
	lateinit var window: AppWindow
	window = AppWindow(
		title = "Jetpack Compose test",
		size = IntSize(720, 480),
		// undecorated = true,
		events = WindowEvents(onResize = {
			val isDocked = window.window.extendedState and JFrame.MAXIMIZED_BOTH == 0
			if(isDockedState != isDocked) isDockedState = isDocked
			hasResized = true
		})
	)
	
	window.window.isResizable = true

//	window.window.contentPane.background = java.awt.Color(255, 255, 255, 0)
//	window.window.background = java.awt.Color(0, 0, 0, 0)
	window.show {
		Providers(
			IsWindowDocked provides isDockedState,
			HasResized provides hasResized
		) {
			DesktopTheme {
				AppTheme {
					AppWindowFrame()
				}
			}
		}
	}
}
