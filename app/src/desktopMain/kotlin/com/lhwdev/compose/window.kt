package com.lhwdev.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf


val IsWindowDocked = ambientOf<Boolean>()

@Composable
val isWindowDocked
    get() = IsWindowDocked.current
