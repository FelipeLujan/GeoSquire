/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.felipelujan.geosquire.ui.common.chat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log

private const val TAG = "ImageUtils"

fun handleImageSelected(
  context: Context,
  uri: Uri,
  onImageSelected: (Bitmap) -> Unit,
  // For some reason, some Android phone would store the picture taken by the camera rotated
  // horizontally. Use this flag to rotate the image back to portrait if the picture's width
  // is bigger than height.
  rotateForPortrait: Boolean = false,
) {
  Log.d(TAG, "Selected URI: $uri")

  val bitmap: Bitmap? =
    try {
      val inputStream = context.contentResolver.openInputStream(uri)
      val tmpBitmap = BitmapFactory.decodeStream(inputStream)
      if (rotateForPortrait && tmpBitmap.width > tmpBitmap.height) {
        val matrix = Matrix()
        matrix.postRotate(90f)
        Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.width, tmpBitmap.height, matrix, true)
      } else {
        tmpBitmap
      }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  if (bitmap != null) {
    onImageSelected(bitmap)
  }
}
