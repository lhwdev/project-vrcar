package com.lhwdev.compose.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lhwdev.lastInstanceOf


/*
 * A tiny library for navigating in compose.
 * I think Route system with level is quite fine, but I can't sure.
 *
 * Selecting appropriate level for a route is very important in this system. Routes with lower levels become 'child
 * node' of with higher levels.
 */

@Stable
data class NavigationState(val routes: SnapshotStateList<Route<*>>)


private val AmbientNavigation = ambientOf<NavigationState>()

val navigationState @Composable get() = AmbientNavigation.current

val currentRoute
	@Composable get() = navigationState.routes.last()

@Composable
inline fun <reified T> currentRoute() = navigationState.routes.lastInstanceOf<T>()


fun NavigationState.pushRoute(route: Route<*>) {
	routes += route
	(route as? RouteListener)?.onRoutePush()
}

fun NavigationState.replaceRoute(route: Route<*>) {
	val level = route.id.level
	
	traverseRoutesIndexed { i, element ->
		if(element.id.level == level) {
			// remove the matching route and descending routes
			routes.drop(i).forEach {
				(it as? RouteListener)?.onRoutePop()
			}
			routes.removeRange(i, routes.lastIndex)
			routes += route
			(route as? RouteListener)?.onRoutePush()
			return
		}
	}
	
	// coming here means: you couldn't find one to replace or across the boundary of higher levels.
	pushRoute(route) // meant 'replace' but there was nothing to replace
}

inline fun NavigationState.traverseRoutesIndexed(level: Int = Int.MAX_VALUE, block: (index: Int, Route<*>) -> Unit) {
	for(i in routes.lastIndex downTo 0) {
		val element = routes[i]
		if(element.id.level > level) break // do not across the routes of which level is higher
		block(i, element)
	}
}

inline fun NavigationState.traverseRoutes(level: Int = Int.MAX_VALUE, block: (Route<*>) -> Unit) {
	for(route in routes.asReversed()) {
		if(route.id.level > level) break // do not across the routes of which level is higher
		block(route)
	}
}

fun NavigationState.popRoute(route: Route<*>? = null) {
	val routes = routes
	val routeToFind = route ?: routes.last()
	if(routes.size == 1) {
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
	Providers(AmbientNavigation provides state) {
		content(state)
	}
}
