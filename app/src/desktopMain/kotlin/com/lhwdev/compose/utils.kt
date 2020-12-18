package com.lhwdev.compose

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.desktop.ComposeWindow
import androidx.compose.runtime.Composable


@Composable
val currentAppWindow get() = AppWindowAmbient.current!!

@Composable
val currentComposeWindow get() = AppWindowAmbient.current!!.window

