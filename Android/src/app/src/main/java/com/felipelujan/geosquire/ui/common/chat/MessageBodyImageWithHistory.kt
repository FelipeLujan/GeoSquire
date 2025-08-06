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

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

/**
 * Composable function to display an image message with history, allowing users to navigate through
 * different versions by sliding on the image.
 */
@Composable
fun MessageBodyImageWithHistory(
  message: ChatMessageImageWithHistory,
  imageHistoryCurIndex: MutableIntState,
) {
  val prevMessage: MutableState<ChatMessageImageWithHistory?> = remember { mutableStateOf(null) }

  LaunchedEffect(message) {
    imageHistoryCurIndex.intValue = message.bitmaps.size - 1
    prevMessage.value = message
  }

  Column {
    val curImage = message.bitmaps[imageHistoryCurIndex.intValue]
    val curImageBitmap = message.imageBitMaps[imageHistoryCurIndex.intValue]

    val bitmapWidth = curImage.width
    val bitmapHeight = curImage.height
    val aspectRatio = bitmapWidth.toFloat() / bitmapHeight.toFloat()

    var value by remember { mutableFloatStateOf(0f) }
    var savedIndex by remember { mutableIntStateOf(0) }
    Image(
      bitmap = curImageBitmap,
      contentDescription = "",
      modifier =
        Modifier
          .height(500.dp) // Large fixed height to prevent vertical cropping
          .widthIn(max = 400.dp) // Maximum width constraint
          .aspectRatio(aspectRatio)
          .pointerInput(Unit) {
          detectHorizontalDragGestures(
            onDragStart = {
              value = 0f
              savedIndex = imageHistoryCurIndex.intValue
            }
          ) { _, dragAmount ->
            value += (dragAmount / 20f) // Adjust sensitivity here
            imageHistoryCurIndex.intValue = (savedIndex + value).toInt()
            if (imageHistoryCurIndex.intValue < 0) {
              imageHistoryCurIndex.intValue = 0
            } else if (imageHistoryCurIndex.intValue > message.bitmaps.size - 1) {
              imageHistoryCurIndex.intValue = message.bitmaps.size - 1
            }
          }
        },
      contentScale = ContentScale.Fit,
    )
  }
}
