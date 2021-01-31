package com.lhwdev.vrcar.client.pages

import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.TextFieldValue
import com.lhwdev.compose.utils.Left
import com.lhwdev.compose.utils.Right


actual fun Modifier.onControlKeyEvent(facade: ControlFacade, textFieldState: MutableState<TextFieldValue>): Modifier {
	fun ControlFacade.handleKey(event: KeyEvent): Boolean {
		when(event.type) {
			KeyEventType.Unknown -> return false
			KeyEventType.KeyDown -> when(event.key) {
				Key.W -> move(Movement.forward)
				Key.S -> move(Movement.backward)
				Key.Left -> direction(Direction.left)
				Key.Right -> direction(Direction.right)
				Key.Equals /* plus */ -> speed = (speed + 0.02f).coerceAtMost(1f)
				Key.Minus -> speed = (speed - 0.02f).coerceAtLeast(0f)
				else -> return false
			}
			KeyEventType.KeyUp -> when(event.key) {
				Key.W, Key.S -> move(null)
				Key.Left, Key.Right -> direction(null)
				else -> return false
			}
		}
		
		return true
	}
	
	val (state, setState) = textFieldState
	
	return onKeyEvent {
		when {
			it.key == Key.Escape -> {
				setState(TextFieldValue())
				true
			}
			state.text.isEmpty() -> facade.handleKey(it)
			else -> false
		}
	}
}
