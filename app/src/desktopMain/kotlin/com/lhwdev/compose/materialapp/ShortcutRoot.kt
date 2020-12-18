package com.lhwdev.compose.materialapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.input.key.ExperimentalKeyInput
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeysSet
import com.lhwdev.compose.currentAppWindow
import com.lhwdev.compose.navigation.NavigationState
import com.lhwdev.compose.navigation.popRoute


private val sShortcuts = mapOf<KeysSet, (AppState, NavigationState) -> Unit>(
	KeysSet(Key.Back) to { _, state -> state.popRoute() },
	KeysSet(setOf(Key.AltLeft, Key.LeftBracket)) to { _, state -> state.popRoute() },
	KeysSet(Key.Escape) to { state, navState -> if(state.info.isTemporary == true) navState.popRoute() }
)

@OptIn(ExperimentalKeyInput::class)
@Composable
actual fun ShortcutRoot(appState: AppState, navigationState: NavigationState, content: @Composable () -> Unit) {
	// this supposes that a shortcut with the same key does not exist in advance
	val window = currentAppWindow
	
	onCommit(navigationState, window) {
		sShortcuts.entries.forEach { (key, shortcut) ->
			window.keyboard.setShortcut(key) { shortcut(appState, navigationState) }
		}
		
		onDispose {
			sShortcuts.keys.forEach { key ->
				window.keyboard.removeShortcut(key)
			}
		}
	}
	
	content()
}
