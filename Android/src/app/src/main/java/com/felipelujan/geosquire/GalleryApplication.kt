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

package com.felipelujan.geosquire

import android.app.Application
import com.felipelujan.geosquire.common.writeLaunchInfo
import com.felipelujan.geosquire.data.DataStoreRepository
import com.felipelujan.geosquire.ui.theme.LanguageSettings
import com.felipelujan.geosquire.ui.theme.ThemeSettings
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GalleryApplication : Application() {

  @Inject lateinit var dataStoreRepository: DataStoreRepository

  override fun onCreate() {
    super.onCreate()

    writeLaunchInfo(context = this)

    // Load saved theme.
    ThemeSettings.themeOverride.value = dataStoreRepository.readTheme()

    // Load saved language.
    LanguageSettings.languageOverride.value = dataStoreRepository.readLanguage()

    FirebaseApp.initializeApp(this)
  }
}
