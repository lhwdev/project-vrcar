package com.lhwdev.compose.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.emptyContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt


/**
 * A TopAppBar displays information and actions relating to the current screen and is placed at the
 * top of the screen.
 *
 * This TopAppBar has slots for a title, navigation icon, and actions. Note that the [title] slot
 * is inset from the start according to spec - for custom use cases such as horizontally
 * centering the title, use the other TopAppBar overload for a generic TopAppBar with no
 * restriction on content.
 *
 * @sample androidx.compose.material.samples.SimpleTopAppBar
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
	title: @Composable () -> Unit,
	topEffect: @Composable () -> Unit = emptyContent(),
	modifier: Modifier = Modifier,
	navigationIcon: @Composable (() -> Unit)? = null,
	actions: @Composable RowScope.() -> Unit = {},
	backgroundColor: Color = MaterialTheme.colors.primarySurface,
	contentColor: Color = contentColorFor(backgroundColor),
	elevation: Dp = TopAppBarElevation
) {
	AppBar(topEffect, backgroundColor, contentColor, elevation, RectangleShape, modifier) {
		val emphasisLevels = AmbientEmphasisLevels.current
		if (navigationIcon == null) {
			Spacer(TitleInsetWithoutIcon)
		} else {
			Row(TitleIconModifier, verticalAlignment = Alignment.CenterVertically) {
				ProvideEmphasis(emphasisLevels.high, navigationIcon)
			}
		}
		
		Row(
			Modifier.fillMaxHeight().weight(1f),
			verticalAlignment = Alignment.CenterVertically
		) {
			ProvideTextStyle(value = MaterialTheme.typography.h6) {
				ProvideEmphasis(emphasisLevels.high, title)
			}
		}
		
		ProvideEmphasis(emphasisLevels.medium) {
			Row(
				Modifier.fillMaxHeight(),
				horizontalArrangement = Arrangement.End,
				verticalAlignment = Alignment.CenterVertically,
				children = actions
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
	topEffect: @Composable () -> Unit = emptyContent(),
	modifier: Modifier = Modifier,
	backgroundColor: Color = MaterialTheme.colors.primarySurface,
	contentColor: Color = contentColorFor(backgroundColor),
	elevation: Dp = TopAppBarElevation,
	content: @Composable RowScope.() -> Unit
) {
	AppBar(
		topEffect,
		backgroundColor,
		contentColor,
		elevation,
		RectangleShape,
		modifier = modifier,
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
	topEffect: @Composable () -> Unit = emptyContent(),
	backgroundColor: Color,
	contentColor: Color,
	elevation: Dp,
	shape: Shape,
	modifier: Modifier = Modifier,
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
				children = children
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
