package com.lhwdev.compose.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lhwdev.lastInstanceOf


/*
 * A tiny library for navigating in compose.
 */

@Stable
data class NavigationState(val routes: SnapshotStateList<Route<*>>)


private val sNavigationAmbient = ambientOf<NavigationState>()

@Composable
val navigationState get() = sNavigationAmbient.current

@Composable
val currentRoute get() = navigationState.routes.last()

@Composable
inline fun <reified T> currentRoute() = navigationState.routes.lastInstanceOf<T>()


fun NavigationState.pushRoute(route: Route<*>) {
	routes += route
	(route as? RouteListener)?.onRoutePush()
}

inline fun NavigationState.traverseRoutesIndexed(level: Int, block: (index: Int, Route<*>) -> Unit) {
	for(i in routes.lastIndex downTo 0) {
		val element = routes[i]
		if(element.id.level > level) break // do not across the routes of which level is higher
		block(i, element)
	}
}

inline fun NavigationState.traverseRoutes(level: Int, block: (Route<*>) -> Unit) {
	traverseRoutesIndexed(level) { _, item -> block(item) }
}

fun NavigationState.popRoute(route: Route<*>? = null) {
	val routes = routes
	val routeToFind = route ?: routes.lastOrNull() ?: run {
		quitApplication()
		return
	}
	val index = findRouteIndex(routeToFind)
	
	if(index == -1) return
	val removed = routes.drop(index)
	routes.removeRange(fromIndex = index, toIndex = routes.size)
	removed.forEach {
		(it as? RouteListener)?.onRoutePop()
	}
}

fun NavigationState.findRouteIndex(route: Route<*>): Int {
	val level = route.id.level
	traverseRoutesIndexed(level) { index, item ->
		if(route == item) return index
	}
	return -1
}

fun NavigationState.hasRoute(route: Route<*>) = findRouteIndex(route) != -1

expect fun quitApplication()


@Composable
fun NavigationRoot(rootRoute: Route<*>, content: @Composable (NavigationState) -> Unit) {
	val state = remember(rootRoute) { NavigationState(routes = mutableStateListOf(rootRoute)) }
	Providers(sNavigationAmbient provides state) {
		content(state)
	}
}
