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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun MessageBodyImage(message: ChatMessageImage, modifier: Modifier = Modifier) {
  val bitmapWidth = message.bitmap.width
  val bitmapHeight = message.bitmap.height
  val aspectRatio = bitmapWidth.toFloat() / bitmapHeight.toFloat()
  
  Image(
    bitmap = message.imageBitMap,
    contentDescription = "",
    modifier = modifier
      .height(500.dp) // Large fixed height to prevent vertical cropping
      .widthIn(max = 400.dp) // Maximum width constraint
      .aspectRatio(aspectRatio),
    contentScale = ContentScale.Fit,
  )
}
