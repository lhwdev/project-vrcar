package com.lhwdev.compose.ui.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf


val IsWindowDocked = ambientOf<Boolean>()

val isWindowDocked @Composable get() = IsWindowDocked.current

val HasResized = ambientOf<Boolean>()

val hasResized @Composable get() = HasResized.current
