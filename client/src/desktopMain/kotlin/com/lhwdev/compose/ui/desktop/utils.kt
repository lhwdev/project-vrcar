package com.lhwdev.compose.ui.desktop

import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.runtime.Composable


@Composable
val currentAppWindow get() = AppWindowAmbient.current!!

@Composable
val currentComposeWindow get() = AppWindowAmbient.current!!.window

