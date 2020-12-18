package com.lhwdev.compose.materialapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.lhwdev.compose.navigation.AbstractRouteId
import com.lhwdev.compose.navigation.ComposableRoute
import com.lhwdev.compose.navigation.ComposableRouteId
import com.lhwdev.compose.navigation.RouteId
import com.lhwdev.compose.utils.Mergeable


@Immutable
data class AppRouteInfo(
	val title: String? = null,
	val isRoot: Boolean? = false,
	val isTemporary: Boolean? = null
) : Mergeable<AppRouteInfo> {
	override fun merge(other: AppRouteInfo) = AppRouteInfo(
		title = other.title ?: title,
		isRoot = other.isRoot ?: isRoot,
		isTemporary = other.isTemporary ?: isTemporary
	)
}


interface AppInfoRouteId<D> : RouteId {
	val info: ((D) -> AppRouteInfo)?
}

interface PureAppInfoRouteId : RouteId {
	val info: AppRouteInfo?
}

fun <D> AppInfoRouteId(
	level: Int, info: ((D) -> AppRouteInfo)?, name: String? = null
): AppInfoRouteId<D> = object : AbstractRouteId(), AppInfoRouteId<D> {
	override val level = level
	override val info = info
	override val name = name
}

fun AppInfoRouteId(
	level: Int, info: AppRouteInfo?, name: String? = null
): PureAppInfoRouteId = object : AbstractRouteId(), PureAppInfoRouteId {
	override val level = level
	override val info = info
	override val name = name
}


class AppRouteId<D>(
	level: Int = 0,
	override val info: ((D) -> AppRouteInfo)? = null,
	name: String? = null,
	content: @Composable (D) -> Unit
) : ComposableRouteId<D>(level, name, content), AppInfoRouteId<D> {
	override fun toString() = "${super.toString()}(level = $level)"
}

fun <D> AppRouteId(
	level: Int = 0,
	info: AppRouteInfo,
	name: String? = null,
	content: @Composable (D) -> Unit
) = AppRouteId(level, { info }, name, content)

operator fun AppRouteId<Unit>.invoke() = AppRoute(this, Unit)

operator fun <D> AppRouteId<D>.invoke(data: D) = AppRoute(this, data)


class AppRoute<D>(
	id: AppRouteId<D>,
	data: D
) : ComposableRoute<D, AppRouteId<D>>(id, data) {
	override fun toString() = "AppRoute(id = $id, +info = ${id.info?.invoke(data)}, data = $data)"
}
