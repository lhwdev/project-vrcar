package com.lhwdev.util


interface DisposeScope {
	fun addDisposal(onDispose: () -> Unit)
}


fun DisposeScope() = DisposeScopeImpl()

class DisposeScopeImpl : DisposeScope {
	private val disposals = mutableListOf<() -> Unit>()
	
	
	override fun addDisposal(onDispose: () -> Unit) {
		disposals += onDispose
	}
	
	fun dispose() {
		disposals.forEach { it() }
	}
}

inline operator fun <T : AutoCloseable> DisposeScope.invoke(block: () -> T) = block().also {
	addDisposal { it.close() }
}
