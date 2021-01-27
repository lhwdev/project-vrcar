@file:Suppress("unused")

package com.lhwdev.compose.utils

import androidx.compose.ui.input.key.Key
import java.awt.event.KeyEvent


val Key.Companion.Left get() = Key(KeyEvent.VK_LEFT)
val Key.Companion.Right get() = Key(KeyEvent.VK_RIGHT)
