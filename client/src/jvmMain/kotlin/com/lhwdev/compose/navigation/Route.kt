package com.lhwdev.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable


@Immutable
interface RouteId {
	val level: Int
	val name: String? get() = null
}

abstract class AbstractRouteId : RouteId {
	override fun toString() = "RouteId $name"
}

fun RouteId(level: Int, name: String? = null) = object : AbstractRouteId() {
	override val level = level
	override val name = name
}

open class ComposableRouteId<in D>(
	override val level: Int,
	override val name: String? = null,
	val content: @Composable (D) -> Unit
) : AbstractRouteId()


@Immutable
interface Route<Id : RouteId> {
	val id: Id
}

interface RouteWithData<D, Id : RouteId> : Route<Id> {
	val data: D
}

fun <D, Id: RouteId> RouteWithData(data: D, id: Id) = object : RouteWithData<D, Id> {
	override val id = id
	override val data = data
}

abstract class AbstractRoute<Id : RouteId> : Route<Id> {
	override fun toString() = "${super.toString()}(id = $id)"
}

interface RouteListener {
	fun onRoutePush()
	fun onRoutePop()
}

fun <Id : RouteId> Route(id: Id) = object : AbstractRoute<Id>() {
	override val id = id
}

open class ComposableRoute<D, Id : ComposableRouteId<D>>(
	override val id: Id,
	override val data: D
) : RouteWithData<D, Id> {
	@Composable
	operator fun invoke() {
		id.content(data)
	}
}
