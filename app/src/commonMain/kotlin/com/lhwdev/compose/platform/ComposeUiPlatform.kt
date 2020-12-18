package com.lhwdev.compose.platform

import androidx.compose.runtime.Composable
import com.lhwdev.compose.materialapp.AppRoute
import com.lhwdev.compose.materialapp.AppRouteInfo


enum class PlatformType { mobile, desktop, web }


val isDesktop get() = ComposeUiPlatform.platformType == PlatformType.desktop

val isMobile get() = ComposeUiPlatform.platformType == PlatformType.mobile

val isWeb get() = ComposeUiPlatform.platformType == PlatformType.web

expect object ComposeUiPlatform {
	val platformType: PlatformType
	fun topEffect(info: AppRouteInfo): @Composable () -> Unit
}
