package com.lhwdev.vrcar.client.pages

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap


actual fun convertBgr888ToRgba8888Bitmap(originalArray: ByteArray, width: Int, height: Int): ImageBitmap {
	val transformedArray = IntArray(originalArray.size * 4 / 3)
	for(i in 0 until originalArray.size / 3) {
		val originalIndex = i * 3
		
		// camera format was BGR; thus index is inverted
		transformedArray[i] = originalArray[originalIndex + 2].toInt().shl(24) or
			originalArray[originalIndex + 1].toInt().shl(16) or
			originalArray[originalIndex + 0].toInt().shl(8) or
			255
	}
	val bitmap = Bitmap.createBitmap(transformedArray, width, height, Bitmap.Config.ARGB_8888)
	return bitmap.asImageBitmap()
}
