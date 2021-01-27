package com.lhwdev.compose.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.jetbrains.skija.Bitmap
import org.jetbrains.skija.ColorAlphaType
import org.jetbrains.skija.ImageInfo
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.nio.ByteBuffer
import java.nio.IntBuffer


fun BufferedImage.toImageBitmap(): ImageBitmap {
	val bitmap = Bitmap()
	bitmap.allocPixels(ImageInfo.makeS32(width, height, ColorAlphaType.PREMUL))
	
	val buffer = IntArray(width * height)
	getRGB(0, 0, width, height, buffer, 0, width)
	
	val pixels = ByteArray(width * height * 4)
	
	var index = 0
	for (y in 0 until height) {
		for (x in 0 until width) {
			val pixel = buffer[y * width + x]
			pixels[index++] = ((pixel and 0xFF)).toByte() // Blue component
			pixels[index++] = (((pixel shr 8) and 0xFF)).toByte() // Green component
			pixels[index++] = (((pixel shr 16) and 0xFF)).toByte() // Red component
			pixels[index++] = (((pixel shr 24) and 0xFF)).toByte() // Alpha component
		}
	}
	
	bitmap.installPixels(bitmap.imageInfo, pixels, (width * 4).toLong())
	return bitmap.asImageBitmap()
	
	/*// 1. fast path: byte[] available
	val dataBuffer = raster.dataBuffer
	val result = if(dataBuffer is DataBufferByte && false) {
		dataBuffer.data
	} else {
		// 2. slow path: get int[] -> convert to byte[]
		val buffer = ByteBuffer.allocate(width * height * Int.SIZE_BYTES)
		buffer.asIntBuffer().put(getRGB(0, 0, width, height, null, 0, width))
		buffer.array()
	}
	bitmap.installPixels(bitmap.imageInfo, result, (width * 4).toLong())
	return bitmap.asImageBitmap()*/
}
