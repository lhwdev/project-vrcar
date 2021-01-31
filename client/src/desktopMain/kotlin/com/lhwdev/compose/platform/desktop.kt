package com.lhwdev.compose.platform

import androidx.compose.runtime.Composable
import com.lhwdev.compose.ui.desktop.TopAppFrame
import com.lhwdev.vrcar.client.AppRouteInfo


actual object ComposeUiPlatform {
	actual val platformType = PlatformType.desktop
	actual fun topEffect(info: AppRouteInfo) = @Composable {
		TopAppFrame(info)
	}
}
