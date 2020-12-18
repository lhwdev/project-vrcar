package com.lhwdev.compose.platform

import androidx.compose.runtime.Composable
import com.lhwdev.compose.TopAppFrame
import com.lhwdev.compose.vrcar.AppRouteInfo


enum class PlatformType { mobile, desktop, web }


val isDesktop get() = ComposeUiPlatform.platformType == PlatformType.desktop

val isMobile get() = ComposeUiPlatform.platformType == PlatformType.mobile

val isWeb get() = ComposeUiPlatform.platformType == PlatformType.web

object ComposeUiPlatform {
	val platformType = PlatformType.desktop
	fun topEffect(info: AppRouteInfo) =
		@Composable { TopAppFrame(info) }
}

