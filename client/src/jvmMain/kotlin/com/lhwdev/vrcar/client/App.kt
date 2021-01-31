package com.lhwdev.vrcar.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lhwdev.compose.AmbientScaffoldState
import com.lhwdev.compose.material.CustomTopAppBar
import com.lhwdev.compose.material.TopEffectorOverlay
import com.lhwdev.compose.navigation.*
import com.lhwdev.compose.platform.ComposeUiPlatform
import com.lhwdev.compose.platform.isMobile
import com.lhwdev.foldMerged
import com.lhwdev.lastInstanceOf
import com.lhwdev.vrcar.client.pages.HomeRoute


@Composable
fun App() {
	NavigationRoot(rootRoute = HomeRoute()) { state ->
		val (drawerState, drawerOpenRoute) = drawerRoute(state)
		
		@OptIn(ExperimentalMaterialApi::class)
		val scaffoldState = remember {
			ScaffoldState(drawerState = drawerState, snackbarHostState = SnackbarHostState())
		}
		
		val routes = state.routes
		val route = routes.lastInstanceOf<ComposableRoute<*, *>>()
		val info = routes.foldMerged(AppRouteInfo()) { element ->
			val id = element.id
			when {
				id is PureAppInfoRouteId ->
					id.info
				element is RouteWithData<*, *> && id is AppInfoRouteId<*> ->
					@Suppress("UNCHECKED_CAST") (id as AppInfoRouteId<in Any?>).info?.invoke(element.data)
				else -> null
			}
		}
		
		val appState = AppState(composableRoute = route, info = info, drawerOpenRoute = drawerOpenRoute)
		info.windowHint?.let { ProvideWindowHint(it) }
		
		Providers(AmbientAppState provides appState, AmbientScaffoldState provides scaffoldState) {
			ShortcutRoot(appState, state) {
				AppRouteRoot(scaffoldState)
			}
		}
	}
}

@Composable
expect fun ProvideWindowHint(hint: WindowHint)

@Composable
expect fun ShortcutRoot(appState: AppState, navigationState: NavigationState, content: @Composable () -> Unit)


@Composable
fun AppRouteRoot(scaffoldState: ScaffoldState) {
	val info = appState.info
	
	TopEffectorOverlay(
		topEffect = ComposeUiPlatform.topEffect(info)
	) { effectHeight ->
		Scaffold(
			topBar = {
				if(info.appUiState?.appBarVisible != false) TopAppBarComponent(
					title = info.title ?: AppInfo.title,
					effectHeight = effectHeight
				)
			},
			scaffoldState = scaffoldState,
			drawerGesturesEnabled = isMobile,
			drawerContent = { DrawerContentComponent(effectHeight) },
		) { paddingValues ->
			Box(Modifier.padding(paddingValues)) {
				appState.composableRoute()
			}
		}
	}
}


private val sDrawerOpenRouteId =
	AppInfoRouteId(level = -10, AppRouteInfo(isRoot = false, isTemporary = true), "drawerOpen")

@Composable
fun drawerRoute(state: NavigationState): Pair<DrawerState, Route<*>> {
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	
	val drawerOpenRoute = state.stateRoute(sDrawerOpenRouteId, drawerState.isOpen) {
		if(it) drawerState.open()
		else drawerState.close()
	}
	
	return drawerState to drawerOpenRoute
}


@Composable
fun TopAppBarComponent(title: String, effectHeight: Dp) {
	val appState = appState
	val navigationState = navigationState
	
	CustomTopAppBar(
		topEffect = {
			Box(Modifier.fillMaxWidth().height(effectHeight))
		},
		title = { Text(title) },
		navigationIcon = {
			IconButton(onClick = {
				navigationState.pushRoute(appState.drawerOpenRoute)
			}) { Icon(Icons.Default.Menu) }
		}
	)
}

@Composable
fun DrawerContentComponent(effectHeight: Dp) {
	Box(
		modifier = Modifier
			.background(MaterialTheme.colors.primary)
			.padding(20.dp, 48.dp + effectHeight, 16.dp, 24.dp)
			.fillMaxWidth()
	) {
		Text("Hello, world!", style = MaterialTheme.typography.h5, color = MaterialTheme.colors.onPrimary)
		
	}
}
