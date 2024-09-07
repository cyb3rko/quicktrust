/*
 * Copyright (c) 2023 Cyb3rKo
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

package de.cyb3rko.quicktrust.managers

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import de.cyb3rko.quicktrust.apps.App
import de.cyb3rko.quicktrust.apps.ApplicationItem

internal object AppManager {
    fun getApplicationItems(context: Context): List<ApplicationItem> {
        val applicationItems = mutableListOf<ApplicationItem>()
        val apps = getApplications(context)

        for (app in apps) {
            val item = ApplicationItem(
                flags = app.flags,
                debuggable = app.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
                isUser = app.flags and ApplicationInfo.FLAG_SYSTEM == 0,
                label = app.packageLabel,
                sdk = app.sdk,
                versionName = app.versionName,
                versionCode = app.versionCode,
                sha = Pair(app.certName, app.certAlgo)
            )
            item.packageName = app.packageName
            applicationItems.add(item)
        }
        return applicationItems
    }

    private fun getApplications(context: Context): List<App> {
        val apps = mutableListOf<App>()
        var packageInfoList = listOf<PackageInfo>()
        try {
            packageInfoList = getInstalledPackages(context)
        } catch (e: Exception) {
            Log.e(
                "QuickTrust",
                "Could not retrieve package info list",
                e
            )
        }

        for (packageInfo in packageInfoList) {
            App.fromPackageInfo(context, packageInfo)?.let {
                apps.add(it)
            }
        }
        return apps
    }

    private fun getInstalledPackages(context: Context): List<PackageInfo> {
        val pm = context.packageManager
        val flagSigningInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val packageInfos = mutableListOf<PackageInfo>()

        packages.forEach {
            packageInfos.add(pm.getPackageInfo(it.packageName, flagSigningInfo))
        }

        return packageInfos
    }
}
