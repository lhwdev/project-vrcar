package com.lhwdev.vrcar.client.pages

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.jetbrains.skija.Bitmap
import org.jetbrains.skija.ColorAlphaType
import org.jetbrains.skija.ColorType
import org.jetbrains.skija.ImageInfo


actual fun convertBgr888ToRgba8888Bitmap(originalArray: ByteArray, width: Int, height: Int): ImageBitmap {
	val transformedArray = ByteArray(originalArray.size * 4 / 3)
	for(i in 0 until originalArray.size / 3) {
		val originalIndex = i * 3
		val newIndex = i * 4
		transformedArray[newIndex] = originalArray[originalIndex + 2] // camera format was BGR; thus index is inverted
		transformedArray[newIndex + 1] = originalArray[originalIndex + 1]
		transformedArray[newIndex + 2] = originalArray[originalIndex + 0]
		transformedArray[newIndex + 3] = 255.toByte()
	}
	val bitmap = Bitmap()
	bitmap.allocPixels(
		ImageInfo(
			width,
			height,
			/*ColorType.RGB_888*/ ColorType.RGBA_8888,
			ColorAlphaType.OPAQUE
		)
	)
	bitmap.installPixels(bitmap.imageInfo, transformedArray, width * 4L)
	return bitmap.asImageBitmap()
}
