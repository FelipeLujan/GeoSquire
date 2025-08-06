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

package com.felipelujan.geosquire.ui.llmchat

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.felipelujan.geosquire.data.TaskType
import com.felipelujan.geosquire.firebaseAnalytics
import com.felipelujan.geosquire.proto.Language
import com.felipelujan.geosquire.ui.common.chat.ChatMessageAudioClip
import com.felipelujan.geosquire.ui.common.chat.ChatMessageImage
import com.felipelujan.geosquire.ui.common.chat.ChatMessageText
import com.felipelujan.geosquire.ui.common.chat.ChatSide
import com.felipelujan.geosquire.ui.common.chat.ChatView
import com.felipelujan.geosquire.ui.common.createTempPictureUri
import com.felipelujan.geosquire.ui.common.chat.handleImageSelected
import com.felipelujan.geosquire.ui.modelmanager.ModelManagerViewModel
import com.felipelujan.geosquire.ui.theme.LanguageSettings

/** Navigation destination data */
object LLMChatDescribeImageDestination {
  val route = "LLMChatDescribeImageRoute"
}

private val LANGUAGE_OPTIONS = listOf(
  Language.LANGUAGE_ENGLISH,
  Language.LANGUAGE_SPANISH,
  Language.LANGUAGE_FRENCH,
  Language.LANGUAGE_ITALIAN,
  Language.LANGUAGE_PORTUGUESE,
  Language.LANGUAGE_GERMAN,
  Language.LANGUAGE_JAPANESE,
  Language.LANGUAGE_CHINESE
)

private fun getGeologicalPrompt(taskType: TaskType, language: Language): String {
  return when (taskType) {
    TaskType.LLM_ASK_HAND_SAMPLE -> when (language) {
      Language.LANGUAGE_ENGLISH -> "You are a geologist with decades of experience, Describe this rock specimen using terms of geology academia, identify predominant and associated minerals, based on properties such as cleavage, luster, Color and Streak, cristal shape"
      Language.LANGUAGE_SPANISH -> "Eres un geólogo con décadas de experiencia, Describe esta muestra de roca utilizando términos de la academia de geología, identifica minerales predominantes y asociados, basándote en propiedades como clivaje, brillo, color y raya, forma cristalina"
      Language.LANGUAGE_FRENCH -> "Vous êtes un géologue avec des décennies d'expérience, Décrivez cet échantillon de roche en utilisant les termes de l'académie de géologie, identifiez les minéraux prédominants et associés, basés sur des propriétés telles que le clivage, l'éclat, la couleur et la rayure, la forme cristalline"
      Language.LANGUAGE_ITALIAN -> "Sei un geologo con decenni di esperienza, Descrivi questo campione di roccia utilizzando i termini dell'accademia di geologia, identifica i minerali predominanti e associati, basandoti su proprietà come sfaldatura, lucentezza, colore e striscia, forma cristallina"
      Language.LANGUAGE_PORTUGUESE -> "Você é um geólogo com décadas de experiência, Descreva esta amostra de rocha usando termos da academia de geologia, identifique minerais predominantes e associados, baseados em propriedades como clivagem, brilho, cor e traço, forma cristalina"
      Language.LANGUAGE_GERMAN -> "Sie sind ein Geologe mit jahrzehntelanger Erfahrung. Beschreiben Sie diese Gesteinsproben unter Verwendung der Begriffe der geologischen Akademie, identifizieren Sie vorherrschende und assoziierte Minerale basierend auf Eigenschaften wie Spaltbarkeit, Glanz, Farbe und Strich, Kristallform"
      Language.LANGUAGE_JAPANESE -> "あなたは数十年の経験を持つ地質学者です。地質学アカデミーの用語を使用してこの岩石標本を記述し、劈開、光沢、色、条痕、結晶形などの特性に基づいて主要および関連鉱物を特定してください"
      Language.LANGUAGE_CHINESE -> "您是一位有数十年经验的地质学家，请使用地质学学术术语描述这个岩石标本，根据解理、光泽、颜色、条痕、晶体形状等特性识别主要和伴生矿物"
      else -> "You are a geologist with decades of experience, Describe this rock specimen using terms of geology academia, identify predominant and associated minerals, based on properties such as cleavage, luster, Color and Streak, cristal shape"
    }
    TaskType.LLM_ANALYZE_OUTCROP -> when (language) {
      Language.LANGUAGE_ENGLISH -> "You are a PhD geologist working out in a field trip, analyze this outcrop and describe what you see in academic and scientific terms, assume that the audience is highly technical. Consider factors like, bandage, rock type, scale (if available), erosion, eathering. Reconstruct the geologic events that resulted in this geologic setting"
      Language.LANGUAGE_SPANISH -> "Eres un geólogo con PhD trabajando en una excursión de campo, analiza este afloramiento y describe lo que ves en términos académicos y científicos, asume que la audiencia es altamente técnica. Considera factores como estratificación, tipo de roca, escala (si está disponible), erosión, meteorización. Reconstruye los eventos geológicos que resultaron en este entorno geológico"
      Language.LANGUAGE_FRENCH -> "Vous êtes un géologue PhD travaillant lors d'une excursion sur le terrain, analysez cet affleurement et décrivez ce que vous voyez en termes académiques et scientifiques, supposez que l'audience est hautement technique. Considérez des facteurs comme la stratification, le type de roche, l'échelle (si disponible), l'érosion, l'altération. Reconstruisez les événements géologiques qui ont abouti à ce cadre géologique"
      Language.LANGUAGE_ITALIAN -> "Sei un geologo con PhD che lavora durante un'escursione sul campo, analizza questo affioramento e descrivi ciò che vedi in termini accademici e scientifici, supponi che il pubblico sia altamente tecnico. Considera fattori come stratificazione, tipo di roccia, scala (se disponibile), erosione, alterazione. Ricostruisci gli eventi geologici che hanno portato a questo contesto geologico"
      Language.LANGUAGE_PORTUGUESE -> "Você é um geólogo com PhD trabalhando em uma excursão de campo, analise este afloramento e descreva o que você vê em termos acadêmicos e científicos, assuma que a audiência é altamente técnica. Considere fatores como estratificação, tipo de rocha, escala (se disponível), erosão, intemperismo. Reconstrua os eventos geológicos que resultaram neste ambiente geológico"
      Language.LANGUAGE_GERMAN -> "Sie sind ein promovierter Geologe bei einer Feldexkursion. Analysieren Sie diesen Aufschluss und beschreiben Sie, was Sie in akademischen und wissenschaftlichen Begriffen sehen. Gehen Sie davon aus, dass das Publikum hochtechnisch ist. Berücksichtigen Sie Faktoren wie Schichtung, Gesteinstyp, Maßstab (falls verfügbar), Erosion, Verwitterung. Rekonstruieren Sie die geologischen Ereignisse, die zu diesem geologischen Umfeld geführt haben"
      Language.LANGUAGE_JAPANESE -> "あなたは野外調査中の地質学博士です。この露頭を分析し、学術的・科学的用語で見たものを説明してください。聴衆は高度に技術的であると仮定してください。成層、岩石タイプ、スケール（利用可能な場合）、浸食、風化などの要因を考慮してください。この地質学的環境をもたらした地質学的事象を再構築してください"
      Language.LANGUAGE_CHINESE -> "您是一位正在进行野外调查的地质学博士，请分析这个露头并用学术和科学术语描述您所看到的，假设听众具有高度的技术背景。考虑成层、岩石类型、规模（如果可用）、侵蚀、风化等因素。重建导致这种地质环境的地质事件"
      else -> "You are a PhD geologist working out in a field trip, analyze this outcrop and describe what you see in academic and scientific terms, assume that the audience is highly technical. Consider factors like, bandage, rock type, scale (if available), erosion, eathering. Reconstruct the geologic events that resulted in this geologic setting"
    }
    else -> when (language) {
      Language.LANGUAGE_ENGLISH -> "Analyze this geological image and provide detailed scientific information."
      Language.LANGUAGE_SPANISH -> "Analiza esta imagen geológica y proporciona información científica detallada."
      Language.LANGUAGE_FRENCH -> "Analysez cette image géologique et fournissez des informations scientifiques détaillées."
      Language.LANGUAGE_ITALIAN -> "Analizza questa immagine geologica e fornisci informazioni scientifiche dettagliate."
      Language.LANGUAGE_PORTUGUESE -> "Analise esta imagem geológica e forneça informações científicas detalhadas."
      Language.LANGUAGE_GERMAN -> "Analysieren Sie dieses geologische Bild und stellen Sie detaillierte wissenschaftliche Informationen bereit."
      Language.LANGUAGE_JAPANESE -> "この地質画像を分析し、詳細な科学的情報を提供してください。"
      Language.LANGUAGE_CHINESE -> "分析这张地质图像并提供详细的科学信息。"
      else -> "Analyze this geological image and provide detailed scientific information."
    }
  }
}

@Composable
fun LLMChatDescribeImageScreen(
  modelManagerViewModel: ModelManagerViewModel,
  navigateUp: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LlmChatViewModelBase,
) {
  val uiState by viewModel.uiState.collectAsState()
  val modelManagerUiState by modelManagerViewModel.uiState.collectAsState()
  val selectedModel = modelManagerUiState.selectedModel
  val messages = uiState.messagesByModel[selectedModel.name] ?: listOf()

  // Show custom start screen when no messages, otherwise show ChatView
  if (messages.isEmpty()) {
    LLMChatDescribeImageStartScreen(
      viewModel = viewModel,
      modelManagerViewModel = modelManagerViewModel,
      navigateUp = navigateUp,
      modifier = modifier,
    )
  } else {
    LLMChatDescribeImageWrapper(
      viewModel = viewModel,
      modelManagerViewModel = modelManagerViewModel,
      navigateUp = navigateUp,
      modifier = modifier,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LLMChatDescribeImageStartScreen(
  viewModel: LlmChatViewModelBase,
  modelManagerViewModel: ModelManagerViewModel,
  navigateUp: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val modelManagerUiState by modelManagerViewModel.uiState.collectAsState()
  val selectedModel = modelManagerUiState.selectedModel
  var selectedLanguage by remember { mutableStateOf(modelManagerViewModel.readLanguageOverride()) }
  var languageDropdownExpanded by remember { mutableStateOf(false) }

  // Photo picker launcher
  val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
    if (uri != null) {
      handleImageSelected(
        context = context,
        uri = uri,
        onImageSelected = { bitmap ->
          val imageMessage = ChatMessageImage(
            bitmap = bitmap,
            imageBitMap = bitmap.asImageBitmap(),
            side = ChatSide.USER
          )
          viewModel.addMessage(model = selectedModel, message = imageMessage)
          
          // Add the geological analysis prompt as a text message using the selected language
          val prompt = getGeologicalPrompt(viewModel.curTask.type, selectedLanguage)
          val textMessage = ChatMessageText(
            content = prompt,
            side = ChatSide.USER
          )
          viewModel.addMessage(model = selectedModel, message = textMessage)
          
          // Generate response automatically
          viewModel.generateResponse(
            model = selectedModel,
            input = textMessage.content,
            images = listOf(bitmap),
            audioMessages = emptyList(),
            onError = {
              viewModel.handleError(
                context = context,
                model = selectedModel,
                modelManagerViewModel = modelManagerViewModel,
                triggeredMessage = textMessage,
              )
            },
          )
          
          firebaseAnalytics?.logEvent(
            "generate_action",
            bundleOf("capability_name" to viewModel.curTask.type.toString(), "model_id" to selectedModel.name),
          )
        }
      )
    }
  }

  // Scaffold with top bar
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("test") },
        navigationIcon = {
          IconButton(onClick = navigateUp) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
      )
    },
    modifier = modifier
  ) { paddingValues ->
    // Custom start screen content - centered UI elements
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
    // Image selection - Only Select Picture option
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
    ) {
      // Select Picture tile (centered, taking available space)
      Card(
        modifier = Modifier
          .fillMaxWidth(0.6f) // Take 60% of available width for better centering
          .height(120.dp)
          .clickable {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
        shape = RoundedCornerShape(16.dp),
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          Icon(
            Icons.Rounded.Photo,
            contentDescription = "",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            "Select Picture",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      // Take Picture tile - REMOVED per user request
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Language selector
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        "Language",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(bottom = 8.dp),
      )
      
      ExposedDropdownMenuBox(
        expanded = languageDropdownExpanded,
        onExpandedChange = { languageDropdownExpanded = !languageDropdownExpanded },
        modifier = Modifier.width(200.dp)
      ) {
        OutlinedTextField(
          modifier = Modifier.menuAnchor().fillMaxWidth(),
          readOnly = true,
          value = LanguageSettings.getDisplayName(selectedLanguage),
          onValueChange = {},
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageDropdownExpanded) },
          colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
          expanded = languageDropdownExpanded,
          onDismissRequest = { languageDropdownExpanded = false },
        ) {
          LANGUAGE_OPTIONS.forEach { language ->
            DropdownMenuItem(
              text = { Text(LanguageSettings.getDisplayName(language)) },
              onClick = {
                selectedLanguage = language
                languageDropdownExpanded = false

                // Update language settings
                LanguageSettings.languageOverride.value = language
                modelManagerViewModel.saveLanguageOverride(language)
              },
              contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            )
          }
        }
      }
    }
  }
}
}

@Composable
fun LLMChatDescribeImageWrapper(
  viewModel: LlmChatViewModelBase,
  modelManagerViewModel: ModelManagerViewModel,
  navigateUp: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  ChatView(
    task = viewModel.curTask,
    viewModel = viewModel,
    modelManagerViewModel = modelManagerViewModel,
    onSendMessage = { model, messages ->
      for (message in messages) {
        viewModel.addMessage(model = model, message = message)
      }

      var text = ""
      val images: MutableList<Bitmap> = mutableListOf()
      val audioMessages: MutableList<ChatMessageAudioClip> = mutableListOf()
      var chatMessageText: ChatMessageText? = null
      for (message in messages) {
        if (message is ChatMessageText) {
          chatMessageText = message
          text = message.content
        } else if (message is ChatMessageImage) {
          images.add(message.bitmap)
        } else if (message is ChatMessageAudioClip) {
          audioMessages.add(message)
        }
      }
      if ((text.isNotEmpty() && chatMessageText != null) || audioMessages.isNotEmpty()) {
        modelManagerViewModel.addTextInputHistory(text)
        viewModel.generateResponse(
          model = model,
          input = text,
          images = images,
          audioMessages = audioMessages,
          onError = {
            viewModel.handleError(
              context = context,
              model = model,
              modelManagerViewModel = modelManagerViewModel,
              triggeredMessage = chatMessageText,
            )
          },
        )

        firebaseAnalytics?.logEvent(
          "generate_action",
          bundleOf("capability_name" to viewModel.curTask.type.toString(), "model_id" to model.name),
        )
      }
    },
    onRunAgainClicked = { model, message ->
      if (message is ChatMessageText) {
        viewModel.runAgain(
          model = model,
          message = message,
          onError = {
            viewModel.handleError(
              context = context,
              model = model,
              modelManagerViewModel = modelManagerViewModel,
              triggeredMessage = message,
            )
          },
        )
      }
    },
    onBenchmarkClicked = { _, _, _, _ -> },
    onResetSessionClicked = { model -> viewModel.resetSession(model = model) },
    showStopButtonInInputWhenInProgress = true,
    onStopButtonClicked = { model -> viewModel.stopResponse(model = model) },
    navigateUp = navigateUp,
    modifier = modifier,
  )
}
