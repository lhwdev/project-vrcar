package com.lhwdev.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.lhwdev.compose.desktop.WindowDraggableArea
import com.lhwdev.compose.materialapp.App
import com.lhwdev.compose.materialapp.AppRoute
import com.lhwdev.compose.materialapp.AppRouteInfo
import com.lhwdev.compose.navigation.navigationState
import com.lhwdev.compose.navigation.popRoute
import com.lhwdev.compose.ui.HoverTextButton
import java.awt.Frame


@Composable
fun AppWindowFrame() {
	Column(
		modifier = Modifier
//			.padding(10.dp) // not supported so far: transparency
//			.drawShadow(10.dp, /*RoundedCornerShape(3.dp)*/)
//			.background(MaterialTheme.colors.primaryVariant)
	) {
		App()
	}
}


private val sFrameHeight = 31.dp


@Composable
fun TopAppFrame(info: AppRouteInfo) { // this is not good, disabling all the native window controls like snapping to edges
	WindowDraggableArea(
		modifier = Modifier
			.fillMaxWidth()
			.height(sFrameHeight)
	) {
		Row(
			modifier = Modifier
				.background(Color.Black.copy(alpha = 0.1f))
				.fillMaxSize()
		) {
			val appWindow = currentAppWindow
			val composerWindow = appWindow.window
			
			val iconColor = MaterialTheme.colors.onPrimary
			
			val navigationState = navigationState
			
			if(info.isRoot != true) WindowFrameButton(onClick = { navigationState.popRoute() }) {
				Icon(Icons.Outlined.ArrowBack, tint = iconColor)
			}
			
			Spacer(Modifier.weight(1f))
			
			WindowFrameButton(onClick = { composerWindow.isVisible = false }) {
				Icon(Icons.Outlined.WindowMinimize, tint = iconColor)
			}
//			com.lhwdev.compose.WindowFrameButton(onClick = { com.lhwdev.compose.materialapp.getCurrentComposeWindow.let {
//				it.extendedState = it.extendedState or JField
//			} }) {
//				Icon(if(com.lhwdev.compose.isWindowDocked) Icons.Outlined.WindowMaximize
//				else Icons.Outlined.WindowDock, tint = Color.White)
//			}
			if(isWindowDocked) WindowFrameButton(onClick = {
				val window = composerWindow
				window.extendedState = window.extendedState or Frame.MAXIMIZED_BOTH
			}) {
				Icon(Icons.Outlined.WindowMaximize, tint = iconColor)
			}
			else WindowFrameButton(onClick = {
				val window = composerWindow
				window.extendedState = window.extendedState and Frame.MAXIMIZED_BOTH.inv()
			}) {
				Icon(Icons.Outlined.WindowDock, tint = iconColor)
			}
			WindowFrameButton(onClick = { appWindow.close() }) {
				Icon(Icons.Outlined.WindowClose, tint = iconColor)
			}
		}
	}
}

@Composable
fun WindowFrameButton(onClick: () -> Unit, content: @Composable () -> Unit) {
	HoverTextButton(
		onClick = onClick,
		shape = RectangleShape,
		modifier = Modifier.aspectRatio(1.48f),
		colors = ButtonConstants.defaultTextButtonColors(Color.Transparent, Color.White.copy(alpha = 0.03f)),
		hoveredColor = Color.White.copy(alpha = 0.10f),
		content = { content() }
	)
}
