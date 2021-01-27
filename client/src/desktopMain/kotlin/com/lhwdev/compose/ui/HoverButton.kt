package com.lhwdev.compose.ui

import androidx.compose.animation.core.animateAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerMoveFilter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HoverButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionState: InteractionState = remember { InteractionState() },
	elevation: ButtonElevation? = ButtonDefaults.elevation(),
	shape: Shape = MaterialTheme.shapes.small,
	border: BorderStroke? = null,
	colors: ButtonColors = ButtonDefaults.buttonColors(),
	hoveredColor: Color =
		(if(colors.backgroundColor(true).luminance() >= 0.5f) Color.White else Color.Black)
			.copy(alpha = 0.02f),
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable RowScope.() -> Unit
) {
	var isHovered by remember { mutableStateOf(false) }
	val colorState by animateAsState(if(isHovered) 1f else 0f)
	
	Button(
		onClick = onClick,
		modifier = modifier.pointerMoveFilter(
			onEnter = {
				isHovered = true
				false
			},
			onExit = {
				isHovered = false
				false
			}
		),
		enabled = enabled,
		interactionState = interactionState,
		elevation = elevation,
		shape = shape,
		border = border,
		colors = object : ButtonColors {
			override fun backgroundColor(enabled: Boolean) =
				hoveredColor.copy(alpha = hoveredColor.alpha * colorState)
					.compositeOver(colors.backgroundColor(enabled))
			
			override fun contentColor(enabled: Boolean) = colors.contentColor(enabled)
		},
		contentPadding = contentPadding,
		content = content
	)
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HoverTextButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionState: InteractionState = remember { InteractionState() },
	elevation: ButtonElevation? = null,
	shape: Shape = MaterialTheme.shapes.small,
	border: BorderStroke? = null,
	colors: ButtonColors = ButtonDefaults.textButtonColors(),
	hoveredColor: Color =
		(if(colors.backgroundColor(true).luminance() >= 0.5f) Color.White else Color.Black)
			.copy(alpha = 0.02f),
	contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
	content: @Composable RowScope.() -> Unit
) = HoverButton(
	onClick,
	modifier,
	enabled,
	interactionState,
	elevation,
	shape,
	border,
	colors,
	hoveredColor,
	contentPadding,
	content
)
