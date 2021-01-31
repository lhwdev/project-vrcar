package com.lhwdev.vrcar.client.pages

import androidx.compose.ui.graphics.ImageBitmap


expect fun convertBgr888ToRgba8888Bitmap(originalArray: ByteArray, width: Int, height: Int): ImageBitmap
