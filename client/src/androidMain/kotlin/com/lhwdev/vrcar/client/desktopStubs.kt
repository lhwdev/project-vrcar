package com.lhwdev.vrcar.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import com.lhwdev.compose.navigation.NavigationState


@Composable
actual fun ProvideWindowHint(hint: WindowHint) {
}

@Composable
actual fun ShortcutRoot(appState: AppState, navigationState: NavigationState, content: @Composable () -> Unit) {
}
