package com.lhwdev.compose.material

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.LayoutDirectionAmbient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


/**
 * Navigation drawers provide access to destinations in your app.
 *
 * Modal navigation drawers block interaction with the rest of an app’s content with a scrim.
 * They are elevated above most of the app’s UI and don’t affect the screen’s layout grid.
 *
 * See [BottomDrawerLayout] for a layout that introduces a bottom drawer, suitable when
 * using bottom navigation.
 *
 * @sample androidx.compose.material.samples.ModalDrawerSample
 *
 * @param drawerContent composable that represents content inside the drawer
 * @param modifier optional modifier for the drawer
 * @param drawerState state of the drawer
 * @param gesturesEnabled whether or not drawer can be interacted by gestures
 * @param drawerShape shape of the drawer sheet
 * @param drawerElevation drawer sheet elevation. This controls the size of the shadow below the
 * drawer sheet
 * @param drawerBackgroundColor background color to be used for the drawer sheet
 * @param drawerContentColor color of the content to use inside the drawer sheet. Defaults to
 * either the matching `onFoo` color for [drawerBackgroundColor], or, if it is not a color from
 * the theme, this will keep the same value set above this Surface.
 * @param scrimColor color of the scrim that obscures content when the drawer is open
 * @param bodyContent content of the rest of the UI
 *
 * @throws IllegalStateException when parent has [Float.POSITIVE_INFINITY] width
 */
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ModalDrawerLayout(
	drawerContent: @Composable ColumnScope.() -> Unit,
	modifier: Modifier = Modifier,
	drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
	gesturesEnabled: Boolean = true,
	drawerShape: Shape = MaterialTheme.shapes.large,
	drawerElevation: Dp = DrawerConstants.DefaultElevation,
	drawerBackgroundColor: Color = MaterialTheme.colors.surface,
	drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
	scrimColor: Color = DrawerConstants.defaultScrimColor,
	bodyContent: @Composable () -> Unit
) {
	WithConstraints(modifier.fillMaxSize()) {
		// TODO : think about Infinite max bounds case
		if(!constraints.hasBoundedWidth) {
			throw IllegalStateException("Drawer shouldn't have infinite width")
		}
		
		val minValue = -constraints.maxWidth.toFloat()
		val maxValue = 0f
		
		val anchors = mapOf(minValue to DrawerValue.Closed, maxValue to DrawerValue.Open)
		val isRtl = LayoutDirectionAmbient.current == LayoutDirection.Rtl
		Box(
			Modifier.swipeable(
				state = drawerState,
				anchors = anchors,
				thresholds = { _, _ -> FractionalThreshold(0.5f) },
				orientation = Orientation.Horizontal,
				enabled = gesturesEnabled,
				reverseDirection = isRtl,
				velocityThreshold = DrawerVelocityThreshold,
				resistance = null
			)
		) {
			Box {
				bodyContent()
			}
			Scrim(
				open = drawerState.isOpen,
				onClose = { drawerState.close() },
				fraction = { calculateFraction(minValue, maxValue, drawerState.offset.value) },
				color = scrimColor
			)
			Surface(
				modifier =
				with(DensityAmbient.current) {
					Modifier.preferredSizeIn(
						minWidth = 344.dp,
						minHeight = constraints.minHeight.toDp(),
						maxWidth = 344.dp,
						maxHeight = constraints.maxHeight.toDp()
					)
				}.offsetPx(x = drawerState.offset).padding(end = VerticalDrawerPadding)
					.tapGestureFilter {}, // prevent dismissing the drawer from clicking drawerContent
				shape = drawerShape,
				color = drawerBackgroundColor,
				contentColor = drawerContentColor,
				elevation = drawerElevation
			) {
				Column(Modifier.fillMaxHeight(), children = drawerContent)
			}
		}
	}
}

private fun calculateFraction(a: Float, b: Float, pos: Float) =
	((pos - a) / (b - a)).coerceIn(0f, 1f)

@Composable
private fun Scrim(
	open: Boolean,
	onClose: () -> Unit,
	fraction: () -> Float,
	color: Color
) {
	val dismissDrawer = if(open) {
		Modifier.tapGestureFilter { onClose() }
	} else {
		Modifier
	}
	
	Canvas(
		Modifier
			.fillMaxSize()
			.then(dismissDrawer)
	) {
		drawRect(color, alpha = fraction())
	}
}

private val VerticalDrawerPadding = 56.dp
private val DrawerVelocityThreshold = 400.dp
