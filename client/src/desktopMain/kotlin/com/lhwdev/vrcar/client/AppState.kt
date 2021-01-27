package com.lhwdev.vrcar.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ambientOf
import com.lhwdev.compose.navigation.ComposableRoute
import com.lhwdev.compose.navigation.Route


@Immutable
data class AppState(val composableRoute: ComposableRoute<*, *>, val info: AppRouteInfo, val drawerOpenRoute: Route<*>)


val AmbientAppState = ambientOf<AppState>()

val appState @Composable get() = AmbientAppState.current
