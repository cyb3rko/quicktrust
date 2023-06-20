/*
 * Copyright (c) 2023 Cyb3rKo
 * Inspired by MuntashirAkon/AppManager:
 * https://github.com/MuntashirAkon/AppManager/blob/688308dcee755f24faa6cefd1a468891064a02e8/app/src/main/java/io/github/muntashirakon/AppManager/db/entity/App.java
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

import android.content.Context
import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import de.cyb3rko.quicktrust.managers.SignatureManager

internal data class App(
    val packageName: String,
    val packageLabel: String,
    val versionName: String,
    val versionCode: Long,
    val flags: Int,
    val sdk: Int,
    val certName: String,
    val certAlgo: String
) {
    companion object {
        fun fromPackageInfo(context: Context, packageInfo: PackageInfo): App {
            val applicationInfo = packageInfo.applicationInfo
            val (issuer, algo) = SignatureManager.getIssuerAndAlg(packageInfo)
            return App(
                packageName = applicationInfo.packageName,
                packageLabel = applicationInfo.loadLabel(context.packageManager).toString(),
                versionName = packageInfo.versionName,
                versionCode = PackageInfoCompat.getLongVersionCode(packageInfo),
                flags = applicationInfo.flags,
                sdk = applicationInfo.targetSdkVersion,
                certName = issuer,
                certAlgo = algo
            )
        }
    }
}
