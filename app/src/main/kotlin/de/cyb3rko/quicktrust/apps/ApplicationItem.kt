/*
 * Copyright (c) 2023 Cyb3rKo
 * Inspired by MuntashirAkon/AppManager
 * https://github.com/MuntashirAkon/AppManager/blob/4ed70a6790bd6089cb1500e645bfb499e24fdb57/app/src/main/java/io/github/muntashirakon/AppManager/main/ApplicationItem.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cyb3rko.quicktrust.apps

import android.content.pm.PackageItemInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

internal data class ApplicationItem(
    var versionName: String,
    var versionCode: Long,
    var flags: Int,
    var label: String,
    var debuggable: Boolean,
    var sdk: Int,
    var sha: Pair<String, String>? = null,
    var isUser: Boolean
) : PackageItemInfo() {
    override fun loadIcon(pm: PackageManager): Drawable {
        return try {
            pm.getApplicationIcon(packageName)
        } catch (ignore: Exception) {
            pm.defaultActivityIcon
        }
    }
}
