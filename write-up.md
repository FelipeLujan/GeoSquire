# GeoSquire: AI-Powered Field Assistant for the Google Gemma 3n Impact Challenge

## Context

• **Business context**: [Google - The Gemma 3n Impact Challenge](https://www.kaggle.com/competitions/google-gemma-3n-hackathon/overview)
• **Competition data**: [Gemma 3n Models and Resources](https://www.kaggle.com/competitions/google-gemma-3n-hackathon/data)

## Overview of the Approach

**GeoSquire** is a state-of-the-art Android application that harnesses Google's Gemma 3n multimodal AI to gear-up geoscientists during field work. Our approach leverages:

**The Problem: Geology Doesn't Happen Next to a Wi-Fi Router**

Geoscientists are detectives of the earth, but their most crucial work happens in the most remote places on the planet—deep in mines, on windswept outcrops, and in unexplored territories far from internet access. 

In these environments, critical decisions that can determine the success of a multi-million dollar exploration project or the accuracy of vital environmental research depend on immediate, expert analysis. Yet, geologists often face a frustrating disconnect:
   - **The Connectivity Blackout:** Remote sites lack the internet needed to access real-time data or collaborative tools.
   - **The Equipment Barrier:** Traditional field analysis requires bulky, expensive equipment, and lab results can take weeks.
   - **The Knowledge Gap:** Junior geologists in the field may lack the experience to make crucial identifications, while senior experts can't be everywhere at once.
This gap between field observation and expert analysis leads to delays, increased costs, and missed opportunities in critical sectors like mineral exploration, environmental monitoring, and geological education.


**The solution: Pocketable, Geo-Tailored AI - GeoSquire**

GeoSquire is a state-of-the-art Android application that harnesses Google's Gemma 3n multimodal AI to deliver sophisticated geological analysis entirely offline. Geoscientists can now use their standard Android device to take a photo of a rock sample or outcrop and receive instant, expert-level insights—no internet, no expensive hardware required.


1. **OFFLINE Hand Sample Rock Classification**: 
Starting with a picture from your camera or load existing photos from gallery
   - **Mineral Identification**: Identify specific minerals in hand-sized rock samples
   - **Rock Type Classification**: Determine if sample is igneous, sedimentary, or metamorphic
   - **Crystal Shape Analysis**: Analyze crystal habits and morphology
   - **Formation Environment**: Determine geological conditions that formed the rock
   - **Physical Properties**: Assess luster, hardness, color, and other diagnostic properties


2. **OFFLINE Outcrop and Field Formation Analysis**:
Starting with a picture from your camera or load existing photos from gallery
   - **Geological Reconstruction**: Analyze exposed rock faces and outcrops during field trips
   - **Structural Analysis**: Identify folds, faults, joints, and bedding planes
   - **Stratigraphic Interpretation**: Determine sequence of geological events
   - **Formation Environment**: Reconstruct ancient depositional and deformation conditions
   - **Field Documentation**: Real-time geological interpretation for field notebooks

3. **OFFLINE Chat with an AI expert in Geology, Geomorphology, Mineraly, and Sedimentology**
   - **Ask questions**: Get expert-level answers on geological topics
   - **Interactive Learning**: Engage in geological discussions and knowledge transfer
    - **Knowledge Transfer**: Facilitate learning for junior geologists through expert interaction


**EXTRA Feature: OFFLINE multilingual conversation and multi-modality**:
   - **English**: Interact with a geo-expert AI, IN ENGLISH, using text and pictures. Even when you are offline.
   - **Español**: Interactue con una IA geo-experta, EN ESPAÑOL, usando texto y fotos. Incluso sin conexión a internet.
   - **Français**: Interagissez avec une IA experte en géologie, EN FRANÇAIS, en utilisant du texte et des photos. Même hors ligne.
   - **Italiano**:  Interagisci con un'IA esperta di geologia, IN ITALIANO, utilizzando testo e foto. Anche offline.
   - **Português**:  Interaja com uma IA especialista em geologia, EM PORTUGUÊS, usando texto e fotos. Mesmo offline.
   - **Deutsch**: Interagieren Sie mit einer Geo-Experten-KI, AUF DEUTSCH, mit Text und Bildern. Auch offline.
   - **日本語**: オフラインでも、テキストと写真を使って地質学専門AIと日本語で対話できます。
   - **中文**: 即使离线也可以使用文本和图片与地质专家AI进行中文交流。



**Why GeoSquire is here to stay:**
- **Ready for future AI multimodal models**: GeoSquire's inference engine built on LiteRT (formerly Tensorflow Lite) allows for future model updates and optimizations without requiring app updates. Feel free to experiment with models from the [LiteRT Community on hugging face](https://huggingface.co/litert-community).
- **Specialized Geological Chain-of-Thought Prompting**: The way how GeosQuire prompts the Gemma 3n model is specifically designed for geological analysis, ensuring accurate and relevant responses.
- **Uptimization of photo input**: Pictures are resized to 512x512 pixels, which is optimal for Gemma 3n's image processing capabilities, ensuring fast and efficient analysis without sacrificing detail.
- **Memory Optimization**: Leveraging Gemma 3n's Per-Layer Embeddings (PLE) architecture for efficient mobile deployment


## Implementation details

### LLM loading and inference
**Implementation Files:**
- `LlmChatModelHelper.kt`: Core MediaPipe LLM infrastructure and multimodal processing
- `LlmChatViewModel.kt`: Geological analysis workflow and response management  
- `LLMChatDescribeImage.kt`: Specialized geological image analysis interface
- `LlmSingleTurnViewModel.kt`: Single-turn geological query processing



#### Core LLM Instantiation and Inference (LlmChatModelHelper.kt)

**Model Initialization and Configuration:**
```kotlin
fun initialize(context: Context, model: Model, onDone: (String) -> Unit) {
  // Extract model configuration parameters
  val maxTokens = model.getIntConfigValue(key = ConfigKey.MAX_TOKENS, defaultValue = DEFAULT_MAX_TOKEN)
  val topK = model.getIntConfigValue(key = ConfigKey.TOPK, defaultValue = DEFAULT_TOPK)
  val topP = model.getFloatConfigValue(key = ConfigKey.TOPP, defaultValue = DEFAULT_TOPP)
  val temperature = model.getFloatConfigValue(key = ConfigKey.TEMPERATURE, defaultValue = DEFAULT_TEMPERATURE)
  val accelerator = model.getStringConfigValue(key = ConfigKey.ACCELERATOR, defaultValue = Accelerator.GPU.label)
  
  // Configure hardware acceleration backend
  val preferredBackend = when (accelerator) {
    Accelerator.CPU.label -> LlmInference.Backend.CPU
    Accelerator.GPU.label -> LlmInference.Backend.GPU
    else -> LlmInference.Backend.GPU
  }
  
  // Build LLM inference options with multimodal support
  val options = LlmInference.LlmInferenceOptions.builder()
    .setModelPath(model.getPath(context = context))
    .setMaxTokens(maxTokens)
    .setPreferredBackend(preferredBackend)
    .setMaxNumImages(if (model.llmSupportImage) MAX_IMAGE_COUNT else 0)
    .build()

  // Create MediaPipe LLM Inference engine
  val llmInference = LlmInference.createFromOptions(context, options)
  
  // Initialize session with geological optimization parameters
  val session = LlmInferenceSession.createFromOptions(
    llmInference,
    LlmInferenceSession.LlmInferenceSessionOptions.builder()
      .setTopK(topK)           // Limited to 40 for geological accuracy
      .setTopP(topP)           // 0.9 for balanced creativity/precision
      .setTemperature(temperature)  // 0.3 for technical accuracy
      .setGraphOptions(
        GraphOptions.builder()
          .setEnableVisionModality(model.llmSupportImage)  // Enable image analysis
          .build()
      )
      .build()
  )
  
  model.instance = LlmModelInstance(engine = llmInference, session = session)
}
```

#### Text-Only Geological Analysis (LlmChatViewModel.kt)

**Single-Turn Text Processing:**
```kotlin
fun generateResponse(model: Model, input: String, onError: () -> Unit) {
  viewModelScope.launch(Dispatchers.Default) {
    // Wait for model initialization
    while (model.instance == null) { delay(100) }
    
    val instance = model.instance as LlmModelInstance
    
    // Calculate token metrics for geological prompts
    val prefillTokens = instance.session.sizeInTokens(input)
    
    // Execute inference with performance monitoring
    LlmChatModelHelper.runInference(
      model = model,
      input = input,
      resultListener = { partialResult, done ->
        // Stream geological analysis results
        response = processLlmResponse("$response$partialResult")
        
        // Update UI with progressive geological insights
        updateResponse(model, promptTemplateType, response)
        
        if (done) setInProgress(false)
      },
      cleanUpListener = { setInProgress(false) }
    )
  }
}
```

#### Multimodal Image-Text Analysis (LLMChatDescribeImage.kt)

**Geological Image Analysis with Specialized Prompting:**
```kotlin
// Geological prompt generation based on task type and language
private fun getGeologicalPrompt(taskType: TaskType, language: Language): String {
  return when (taskType) {
    TaskType.LLM_ASK_HAND_SAMPLE -> when (language) {
      Language.LANGUAGE_ENGLISH -> "You are a geologist with decades of experience. Describe this rock specimen using terms of geology academia, identify predominant and associated minerals, based on properties such as cleavage, luster, color and streak, crystal shape"
      Language.LANGUAGE_SPANISH -> "Eres un geólogo con décadas de experiencia. Describe esta muestra de roca utilizando términos de la academia de geología, identifica minerales predominantes y asociados..."
      // Additional language support for Portuguese, French, Italian
    }
    TaskType.LLM_ANALYZE_OUTCROP -> when (language) {
      Language.LANGUAGE_ENGLISH -> "You are a field geologist analyzing rock formations. Describe the structural features, formation environment, and geological history visible in this outcrop..."
      // Multilingual outcrop analysis prompts
    }
  }
}

// Multimodal inference execution
val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
  if (uri != null) {
    handleImageSelected(context = context, uri = uri) { bitmap ->
      // Add image to chat context
      val imageMessage = ChatMessageImage(bitmap = bitmap, side = ChatSide.USER)
      viewModel.addMessage(model = selectedModel, message = imageMessage)
      
      // Generate specialized geological prompt
      val prompt = getGeologicalPrompt(viewModel.curTask.type, selectedLanguage)
      val textMessage = ChatMessageText(content = prompt, side = ChatSide.USER)
      viewModel.addMessage(model = selectedModel, message = textMessage)
      
      // Execute multimodal geological analysis
      viewModel.generateResponse(
        model = selectedModel,
        input = textMessage.content,
        images = listOf(bitmap),  // Single image input for Gemma 3n
        audioMessages = emptyList(),
        onError = { /* Error handling */ }
      )
    }
  }
}
```

#### Advanced Multimodal Processing (LlmChatModelHelper.kt)

**Image and Text Integration for Geological Analysis:**
```kotlin
fun runInference(
  model: Model,
  input: String,
  resultListener: ResultListener,
  cleanUpListener: CleanUpListener,
  images: List<Bitmap> = listOf(),
  audioClips: List<ByteArray> = listOf()
) {
  val instance = model.instance as LlmModelInstance
  val session = instance.session
  
  // MediaPipe requires text before image for optimal geological analysis
  if (input.trim().isNotEmpty()) {
    session.addQueryChunk(input)  // Add geological analysis prompt
  }
  
  // Convert Android Bitmap to MediaPipe format for processing
  for (image in images) {
    session.addImage(BitmapImageBuilder(image).build())  // Process geological images
  }
  
  // Future audio support for field recordings
  for (audioClip in audioClips) {
    // session.addAudio(audioClip)  // Geological field audio analysis
  }
  
  // Execute async inference with streaming results
  session.generateResponseAsync(resultListener)
}
```


2. **Multilingual Geological Intelligence**
   ```kotlin
   // Multilingual geological analysis support
   object LanguageSettings {
     var languageOverride = mutableStateOf(Language.LANGUAGE_ENGLISH)
     
     fun getSupportedLanguages(): List<Language> = listOf(
       Language.LANGUAGE_ENGLISH,   // Full geological terminology
       Language.LANGUAGE_SPANISH,   // Análisis geológico completo  
       Language.LANGUAGE_FRENCH,    // Analyse géologique complète
       Language.LANGUAGE_ITALIAN,   // Analisi geologica completa
       Language.LANGUAGE_PORTUGUESE, // Análise geológica completa
       Language.LANGUAGE_GERMAN,    // Vollständige geologische Analyse
       Language.LANGUAGE_JAPANESE,  // 完全な地質学的分析
       Language.LANGUAGE_CHINESE    // 完整的地质分析
     )
   }
   ```

3. **Task-Specific Geological Features**
   ```kotlin
   // Specific geological task implementations
   val TASK_LLM_ASK_HAND_SAMPLE = Task(
     type = TaskType.LLM_ASK_HAND_SAMPLE,
     description = "Take a close-up picture of a hand sample to identify the predominant and associated minerals",
     // Analyzes: minerals, rock type, crystal shape, formation environment, luster
   )
   
   val TASK_LLM_ANALYZE_OUTCROP = Task(
     type = TaskType.LLM_ANALYZE_OUTCROP, 
     description = "Take a picture of an outcrop to identify its genesis and formation",
     // Provides: geological reconstruction of formation events
   )
   ```

### Next Steps

**Expandin multimodality:**
- **audio and video processing**: Desktop versions of Gemma3n are already capable of processing audio and video, it's entirely possible to extend GeoSquire to support audio and video analysis to examine samples and outcrops from multiple angles, or to analyze geological field recordings.
- **AI Field trip Journal**: 
  - An intelligent field journal that automatically documents geological observations, integrates with geological maps, and provides real-time analysis of field data.
- **AI Glasses implementation**: Stream video feed from smart glasses to GeoSquire for hands-free geological analysis, allowing geologists to focus on fieldwork while receiving real-time insights.


## Sources

### Research Papers and Technical Documentation
- [Gemma 3n: Technical Report](https://www.kaggle.com/models/google/gemma-3n/) - Core model architecture and capabilities
- [MediaPipe LLM Inference API Documentation](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference) - On-device AI implementation
- [Android AI Edge Gallery Framework](https://github.com/google-ai-edge/gallery) - Base application architecture

### Mineralogical and Industry References  
- [Mindat.org - The Mineral Database](https://www.mindat.org/) - World's largest database of minerals, rocks, and meteorites for geological reference

### Competition and Technical Resources
- [Google Gemma 3n Impact Challenge](https://www.kaggle.com/competitions/google-gemma-3n-hackathon) - Competition guidelines and requirements
- [LiteRT Model Optimization](https://www.tensorflow.org/lite) - Mobile AI deployment best practices
- [Android Jetpack Compose Documentation](https://developer.android.com/jetpack/compose) - Modern Android UI framework

---

**GeoSquire represents a paradigm shift in geological field work. By leveraging Gemma 3n's unique capabilities—Per-Layer Embeddings for memory efficiency, multimodal understanding for image analysis, and offline-first architecture—we've created the first truly intelligent geological field assistant. This isn't just an incremental improvement; it's a fundamental transformation of how geoscientists interact with and understand the Earth beneath our feet.**

*Built with ⛏️ using Gemma 3n • Powered by Google AI Edge • Designed for Geological Discovery*