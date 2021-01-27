package com.lhwdev.compose.ui.desktop

import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.runtime.Composable


val currentAppWindow @Composable get() = AppWindowAmbient.current!!

val currentComposeWindow @Composable get() = AppWindowAmbient.current!!.window

