@file:JvmName("NavigationJvm")

package com.lhwdev.compose.navigation

import androidx.compose.desktop.AppManager


actual fun quitApplication() {
	AppManager.exit()
}
