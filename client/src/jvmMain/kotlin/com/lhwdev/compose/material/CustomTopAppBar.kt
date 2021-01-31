package com.lhwdev.compose.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.emptyContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * A TopAppBar displays information and actions relating to the current screen and is placed at the
 * top of the screen.
 *
 * This TopAppBar has slots for a title, navigation icon, and actions. Note that the [title] slot
 * is inset from the start according to spec - for custom use cases such as horizontally
 * centering the title, use the other TopAppBar overload for a generic TopAppBar with no
 * restriction on content.
 *
 * @param title The title to be displayed in the center of the TopAppBar
 * @param navigationIcon The navigation icon displayed at the start of the TopAppBar. This should
 * typically be an [IconButton] or [IconToggleButton].
 * @param actions The actions displayed at the end of the TopAppBar. This should typically be
 * [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param backgroundColor The background color for the TopAppBar. Use [Color.Transparent] to have
 * no color.
 * @param contentColor The preferred content color provided by this TopAppBar to its children.
 * Defaults to either the matching `onFoo` color for [backgroundColor], or if [backgroundColor]
 * is not a color from the theme, this will keep the same value set above this TopAppBar.
 * @param elevation the elevation of this TopAppBar.
 */
@Composable
fun CustomTopAppBar(
	modifier: Modifier = Modifier,
	title: @Composable () -> Unit,
	topEffect: @Composable () -> Unit = emptyContent(),
	navigationIcon: @Composable (() -> Unit)? = null,
	actions: @Composable RowScope.() -> Unit = {},
	backgroundColor: Color = MaterialTheme.colors.primarySurface,
	contentColor: Color = contentColorFor(backgroundColor),
	elevation: Dp = TopAppBarElevation
) {
	AppBar(modifier, topEffect, backgroundColor, contentColor, elevation, RectangleShape) {
		if (navigationIcon == null) {
			Spacer(TitleInsetWithoutIcon)
		} else {
			Row(TitleIconModifier, verticalAlignment = Alignment.CenterVertically) {
				Providers(AmbientContentAlpha provides ContentAlpha.high, content = navigationIcon)
			}
		}
		
		Row(
			Modifier.fillMaxHeight().weight(1f),
			verticalAlignment = Alignment.CenterVertically
		) {
			ProvideTextStyle(value = MaterialTheme.typography.h6) {
				Providers(AmbientContentAlpha provides ContentAlpha.high, content = title)
			}
		}
		
		Providers(AmbientContentAlpha provides ContentAlpha.medium) {
			Row(
				Modifier.fillMaxHeight(),
				horizontalArrangement = Arrangement.End,
				verticalAlignment = Alignment.CenterVertically,
				content = actions
			)
		}
	}
}

/**
 * A TopAppBar displays information and actions relating to the current screen and is placed at the
 * top of the screen.
 *
 * This TopAppBar has no pre-defined slots for content, allowing you to customize the layout of
 * content inside.
 *
 * @param backgroundColor The background color for the TopAppBar. Use [Color.Transparent] to have
 * no color.
 * @param contentColor The preferred content color provided by this TopAppBar to its children.
 * Defaults to either the matching `onFoo` color for [backgroundColor], or if [backgroundColor] is
 * not a color from the theme, this will keep the same value set above this TopAppBar.
 * @param elevation the elevation of this TopAppBar.
 * @param content the content of this TopAppBar.The default layout here is a [Row],
 * so content inside will be placed horizontally.
 */
@Composable
fun CustomTopAppBar(
	modifier: Modifier = Modifier,
	topEffect: @Composable () -> Unit = emptyContent(),
	backgroundColor: Color = MaterialTheme.colors.primarySurface,
	contentColor: Color = contentColorFor(backgroundColor),
	elevation: Dp = TopAppBarElevation,
	content: @Composable RowScope.() -> Unit
) {
	AppBar(
		modifier,
		topEffect,
		backgroundColor,
		contentColor,
		elevation,
		RectangleShape,
		children = content
	)
}

/**
 * An empty App Bar that expands to the parent's width.
 *
 * For an App Bar that follows Material spec guidelines to be placed on the top of the screen, see
 * [TopAppBar].
 */
@Composable
private fun AppBar(
	modifier: Modifier = Modifier,
	topEffect: @Composable () -> Unit = emptyContent(),
	backgroundColor: Color,
	contentColor: Color,
	elevation: Dp,
	shape: Shape,
	children: @Composable RowScope.() -> Unit
) {
	Surface(
		color = backgroundColor,
		contentColor = contentColor,
		elevation = elevation,
		shape = shape,
		modifier = modifier
	) {
		Column {
			topEffect()
			
			Row(
				Modifier.fillMaxWidth()
					.padding(start = AppBarHorizontalPadding, end = AppBarHorizontalPadding)
					.preferredHeight(AppBarHeight),
				horizontalArrangement = Arrangement.SpaceBetween,
				content = children
			)
		}
	}
}

private val AppBarHeight = 56.dp
// TODO: this should probably be part of the touch target of the start and end icons, clarify this
private val AppBarHorizontalPadding = 4.dp
// Start inset for the title when there is no navigation icon provided
private val TitleInsetWithoutIcon = Modifier.preferredWidth(16.dp - AppBarHorizontalPadding)
// Start inset for the title when there is a navigation icon provided
private val TitleIconModifier = Modifier.fillMaxHeight()
	.preferredWidth(72.dp - AppBarHorizontalPadding)

// TODO: clarify elevation in surface mapping - spec says 0.dp but it appears to have an
//  elevation overlay applied in dark theme examples.
private val TopAppBarElevation = 4.dp
