package com.lhwdev.util


interface Stack<T> : MutableCollection<T> {
	fun pushFirst(element: T)
	fun popFirst(): T
	fun peekFirst(): T
}

interface Queue<T> : MutableCollection<T> {
	fun pushLast(element: T)
	fun popFirst(): T
	fun peekFirst(): T
}

interface Deque<T> : Stack<T>, Queue<T>

private class DequeImpl<T>(array: Array<@UnsafeVariance T>, growFactor: Float = 1.25f) :
	CyclingArrayList<T>(array, growFactor), Deque<T> {
	
	constructor(capacity: Int = sDefaultCapacity, growFactor: Float = 1.25f) :
		this(@OptIn(UnsafeArray::class) unsafeArrayOf(capacity), growFactor)
	
	@OptIn(UnsafeArray::class)
	constructor(collection: Collection<T>, capacity: Int = collection.size) : this(collection.toArrayUnsafe(capacity)) {
		size = collection.size
	}
	
	override fun pushFirst(element: T) {
		add(0, element)
	}
	
	override fun pushLast(element: T) {
		add(element)
	}
	
	override fun popFirst() = removeAt(0)
	override fun peekFirst() = get(0)
}

fun <T> stackOf(): Stack<T> = DequeImpl()
fun <T> stackOf(vararg elements: T): Stack<T> = DequeImpl(elements.asList())

fun <T> queueOf(): Queue<T> = DequeImpl()
fun <T> queueOf(vararg elements: T): Queue<T> = DequeImpl(elements.asList())

fun <T> dequeOf(): Deque<T> = DequeImpl()
fun <T> dequeOf(vararg elements: T): Deque<T> = DequeImpl(elements.asList())

