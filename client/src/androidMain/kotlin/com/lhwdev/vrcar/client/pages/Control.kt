package com.lhwdev.vrcar.client.pages

import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue


actual fun Modifier.onControlKeyEvent(facade: ControlFacade, textFieldState: MutableState<TextFieldValue>) = this
