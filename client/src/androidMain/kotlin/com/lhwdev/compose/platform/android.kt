package com.lhwdev.compose.platform

import androidx.compose.runtime.Composable
import com.lhwdev.vrcar.client.AppRouteInfo


actual object ComposeUiPlatform {
	actual val platformType = PlatformType.mobile
	actual fun topEffect(info: AppRouteInfo) = @Composable {}
}

