package com.lhwdev.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import com.lhwdev.lastInstanceOf


inline fun <reified T : Route<*>> NavigationState.routeOf() = routes.lastInstanceOf<T>()

inline fun <reified T : RouteId> NavigationState.routeIdOf() = routes.last { it.id is T }.id as T

/**
 * Creates a route that is synchronized with the provided [value].
 * When pushing the returned route to the navigation stack, [setValue] is called with `true` and popping, with `false`.
 * If [value] is updated, [stateRoute] pushes or pops the root automatically.
 */
@Composable
fun <T : RouteId> NavigationState.stateRoute(id: T, value: Boolean, setValue: (Boolean) -> Unit): Route<T> {
	val route = remember(id) {
		object : AbstractRoute<T>(), RouteListener {
			var valueRef = value
			override val id: T = id
			
			override fun onRoutePush() {
				if(!valueRef) setValue(true)
			}
			
			override fun onRoutePop() {
				if(valueRef) setValue(false)
			}
		}
	}
	
	onCommit(value) {
		route.valueRef = value // [value] in object : Route<T>, ... is captured once it is remembered via `remember(id) { ... }`
		
		if(value && !hasRoute(route))
			pushRoute(route)
		else if(!value && hasRoute(route))
			popRoute(route)
	}
	
	return route
}
