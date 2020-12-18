package com.lhwdev.compose.materialapp.pages

import androidx.compose.runtime.Composable
import com.lhwdev.compose.materialapp.AppInfo
import com.lhwdev.compose.materialapp.AppRouteId
import com.lhwdev.compose.materialapp.AppRouteInfo


val HomeRoute = AppRouteId<Unit>(info = AppRouteInfo(title = AppInfo.title, isRoot = true), name = "Home") {
	Home()
}


@Composable
fun Home() {

}
