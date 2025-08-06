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

package com.felipelujan.geosquire.ui.modelmanager

// import androidx.compose.ui.tooling.preview.Preview
// import com.felipelujan.geosquire.ui.preview.PreviewModelManagerViewModel
// import com.felipelujan.geosquire.ui.preview.TASK_TEST1
// import com.felipelujan.geosquire.ui.theme.GalleryTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.felipelujan.geosquire.data.Model
import com.felipelujan.geosquire.data.Task
import com.felipelujan.geosquire.data.TaskType
import com.felipelujan.geosquire.ui.common.modelitem.ModelItem

private const val TAG = "AGModelList"

/** The list of models in the model manager. */
@Composable
fun ModelList(
  task: Task,
  modelManagerViewModel: ModelManagerViewModel,
  contentPadding: PaddingValues,
  onModelClicked: (Model) -> Unit,
  modifier: Modifier = Modifier,
) {
  // This is just to update "models" list when task.updateTrigger is updated so that the UI can
  // be properly updated.
  val models by
    remember(task) {
      derivedStateOf {
        val trigger = task.updateTrigger.value
        if (trigger >= 0) {
          task.models.toList().filter { !it.imported }
        } else {
          listOf()
        }
      }
    }
  val importedModels by
    remember(task) {
      derivedStateOf {
        val trigger = task.updateTrigger.value
        if (trigger >= 0) {
          task.models.toList().filter { it.imported }
        } else {
          listOf()
        }
      }
    }

  val listState = rememberLazyListState()

  Box(contentAlignment = Alignment.BottomEnd) {
    LazyColumn(
      modifier = modifier.padding(top = 8.dp),
      contentPadding = contentPadding,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      state = listState,
    ) {
      // Headline.
      item(key = "headline") {
        Text(
          task.description,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
      }

      // Task image (if available).
      task.imageResourceId?.let { imageRes ->
        item(key = "task_image") {
          Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Task illustration",
            modifier = Modifier
              .fillMaxWidth()
              .height(400.dp) // Increased from 200dp to prevent vertical cropping
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit // Changed from Crop to Fit to show full image
          )
        }
      }

      // URLs - Hidden per user request
      /*
      item(key = "urls") {
        Row(
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp),
        ) {
          Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            if (task.docUrl.isNotEmpty()) {
              val linkText = if (task.type == TaskType.LLM_ASK_IMAGE || task.type == TaskType.LLM_ASK_HAND_SAMPLE) {
                "Download Google's latest AI model for offline chat, Gemma3n"
              } else {
                "API Documentation"
              }
              ClickableLink(
                url = task.docUrl,
                linkText = linkText,
                icon = Icons.Outlined.Description,
              )
            }
            if (task.sourceCodeUrl.isNotEmpty()) {
              ClickableLink(
                url = task.sourceCodeUrl,
                linkText = "Example code",
                icon = Icons.Outlined.Code,
              )
            }
          }
        }
      }
      */

      // List of models within a task.
      items(items = models) { model ->
        Box {
          ModelItem(
            model = model,
            task = task,
            modelManagerViewModel = modelManagerViewModel,
            onModelClicked = onModelClicked,
            modifier = Modifier.padding(horizontal = 12.dp),
          )
        }
      }

      // Title for imported models.
      if (importedModels.isNotEmpty()) {
        item(key = "importedModelsTitle") {
          Text(
            "Imported models",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp),
          )
        }
      }

      // List of imported models within a task.
      items(items = importedModels, key = { it.name }) { model ->
        Box {
          ModelItem(
            model = model,
            task = task,
            modelManagerViewModel = modelManagerViewModel,
            onModelClicked = onModelClicked,
            modifier = Modifier.padding(horizontal = 12.dp),
          )
        }
      }
    }
  }
}

// @Preview(showBackground = true)
// @Composable
// fun ModelListPreview() {
//   val context = LocalContext.current

//   GalleryTheme {
//     ModelList(
//       task = TASK_TEST1,
//       modelManagerViewModel = PreviewModelManagerViewModel(context = context),
//       onModelClicked = {},
//       contentPadding = PaddingValues(all = 16.dp),
//     )
//   }
// }
