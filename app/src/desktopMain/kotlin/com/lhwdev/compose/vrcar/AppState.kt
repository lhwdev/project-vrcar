package com.lhwdev.compose.vrcar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import com.lhwdev.compose.navigation.ComposableRoute
import com.lhwdev.compose.navigation.Route


@Immutable
data class AppState(val composableRoute: ComposableRoute<*, *>, val info: AppRouteInfo, val drawerOpenRoute: Route<*>)


private val sAppStateAmbient = ambientOf<AppState>()


@Composable
fun ProvideAppState(appState: AppState, children: @Composable () -> Unit) {
	Providers(sAppStateAmbient provides appState, children = children)
}

@Composable
val appState get() = sAppStateAmbient.current
