package com.lhwdev.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.path


private val sWindowClose by lazy {
	materialIcon("WindowClose") {
		path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
			strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter, strokeLineMiter = 4.0f,
			pathFillType = PathFillType.NonZero) {
			moveTo(13.46f, 12.0f)
			lineTo(19.0f, 17.54f)
			verticalLineTo(19.0f)
			horizontalLineTo(17.54f)
			lineTo(12.0f, 13.46f)
			lineTo(6.46f, 19.0f)
			horizontalLineTo(5.0f)
			verticalLineTo(17.54f)
			lineTo(10.54f, 12.0f)
			lineTo(5.0f, 6.46f)
			verticalLineTo(5.0f)
			horizontalLineTo(6.46f)
			lineTo(12.0f, 10.54f)
			lineTo(17.54f, 5.0f)
			horizontalLineTo(19.0f)
			verticalLineTo(6.46f)
			lineTo(13.46f, 12.0f)
			close()
		}
	}
}

val Icons.Outlined.WindowClose get() = sWindowClose


private val sWindowDock by lazy {
	materialIcon("WindowDock") {
		path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
			strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter, strokeLineMiter = 4.0f,
			pathFillType = PathFillType.NonZero) {
			moveTo(18.0f, 18.0f)
			verticalLineTo(20.0f)
			horizontalLineTo(4.0f)
			arcTo(2.0f, 2.0f, 0.0f, false, true, 2.0f, 18.0f)
			verticalLineTo(8.0f)
			horizontalLineTo(4.0f)
			verticalLineTo(18.0f)
			moveTo(22.0f, 6.0f)
			verticalLineTo(14.0f)
			arcTo(2.0f, 2.0f, 0.0f, false, true, 20.0f, 16.0f)
			horizontalLineTo(8.0f)
			arcTo(2.0f, 2.0f, 0.0f, false, true, 6.0f, 14.0f)
			verticalLineTo(6.0f)
			arcTo(2.0f, 2.0f, 0.0f, false, true, 8.0f, 4.0f)
			horizontalLineTo(20.0f)
			arcTo(2.0f, 2.0f, 0.0f, false, true, 22.0f, 6.0f)
			moveTo(20.0f, 6.0f)
			horizontalLineTo(8.0f)
			verticalLineTo(14.0f)
			horizontalLineTo(20.0f)
			close()
		}
	}
}

val Icons.Outlined.WindowDock get() = sWindowDock


private val sWindowMaximize by lazy {
	materialIcon("WindowMaximize") {
		path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
			strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter, strokeLineMiter = 4.0f,
			pathFillType = PathFillType.NonZero) {
			moveTo(4.0f, 4.0f)
			horizontalLineTo(20.0f)
			verticalLineTo(20.0f)
			horizontalLineTo(4.0f)
			verticalLineTo(4.0f)
			moveTo(6.0f, 8.0f)
			verticalLineTo(18.0f)
			horizontalLineTo(18.0f)
			verticalLineTo(8.0f)
			horizontalLineTo(6.0f)
			close()
		}
	}
}

val Icons.Outlined.WindowMaximize get() = sWindowMaximize


private val sWindowMinimize by lazy {
	materialIcon("WindowMinimize") {
		path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
			strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter, strokeLineMiter = 4.0f,
			pathFillType = PathFillType.NonZero) {
			moveTo(20.0f, 14.0f)
			horizontalLineTo(4.0f)
			verticalLineTo(10.0f)
			horizontalLineTo(20.0f)
		}
	}
}

val Icons.Outlined.WindowMinimize get() = sWindowMinimize
