package com.lhwdev.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.currentComposer


@OptIn(ComposeCompilerApi::class)
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun isChanged(value: Any?): Boolean {
	// Do not inline this by your hand; that would cause an error.
	// start/endReplaceableGroup is inserted surrounding `fun isChanged`.
	return currentComposer.changed(value)
}
