package com.lhwdev.compose.platform

import androidx.compose.runtime.Composable
import com.lhwdev.compose.TopAppFrame
import com.lhwdev.compose.materialapp.AppRouteInfo


actual object ComposeUiPlatform {
    actual val platformType = PlatformType.desktop
    actual fun topEffect(info: AppRouteInfo) =
        @Composable { TopAppFrame(info) }
}
