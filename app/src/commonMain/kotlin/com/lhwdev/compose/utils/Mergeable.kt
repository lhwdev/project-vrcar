package com.lhwdev.compose.utils


/**
 * Describes a class which can be merged with other instance.
 * Normally type `<T>` refers to the class itself.
 *
 * While merging, `other` should take precedence over `this`.
 */
interface Mergeable<T> {
	/**
	 * Merges `this` and [other].
	 * While merging, [other] should take precedence over `this`.
	 *
	 * Example:
	 * ```kotlin
	 * data class TextStyle(val color: Color? = null, val size: Float? = null) : Mergeable<TextStyle> {
	 *     override fun merge(other: TextStyle) = TextStyle(
	 *         color = other.color ?: color,
	 *         size = other.size ?: size
	 *     )
	 * }
	 * ```
	 */
	fun merge(other: T): T
}
