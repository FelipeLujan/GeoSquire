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

package com.felipelujan.geosquire.ui.theme

import androidx.compose.runtime.mutableStateOf
import com.felipelujan.geosquire.proto.Language
import java.util.Locale

object LanguageSettings {
  var languageOverride = mutableStateOf(Language.LANGUAGE_ENGLISH)
  
  fun getLocale(language: Language): Locale {
    return when (language) {
      Language.LANGUAGE_SPANISH -> Locale("es")
      Language.LANGUAGE_FRENCH -> Locale("fr")
      Language.LANGUAGE_ITALIAN -> Locale("it")
      Language.LANGUAGE_PORTUGUESE -> Locale("pt")
      Language.LANGUAGE_GERMAN -> Locale("de")
      Language.LANGUAGE_JAPANESE -> Locale("ja")
      Language.LANGUAGE_CHINESE -> Locale("zh")
      else -> Locale.ENGLISH
    }
  }
  
  fun getDisplayName(language: Language): String {
    return when (language) {
      Language.LANGUAGE_ENGLISH -> "English"
      Language.LANGUAGE_SPANISH -> "Español"
      Language.LANGUAGE_FRENCH -> "Français"
      Language.LANGUAGE_ITALIAN -> "Italiano"
      Language.LANGUAGE_PORTUGUESE -> "Português"
      Language.LANGUAGE_GERMAN -> "Deutsch"
      Language.LANGUAGE_JAPANESE -> "日本語"
      Language.LANGUAGE_CHINESE -> "中文"
      else -> "English"
    }
  }
}
