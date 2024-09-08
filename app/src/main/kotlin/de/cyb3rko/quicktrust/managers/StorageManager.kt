/*
 * Copyright (c) 2023-2024 Cyb3rKo
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
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import java.io.File
import java.io.FileOutputStream

internal object StorageManager {
    fun launchFileSelector(resultLauncher: ActivityResultLauncher<Intent>) {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            resultLauncher.launch(this)
        }
    }

    fun copyTempFile(context: Context, uri: Uri, apk: Boolean): String? {
        val tempBytes: ByteArray
        val fileName = getUriFileName(context, uri) ?: "temp.apk"
        if (apk && !fileName.endsWith(".apk")) {
            return null
        }
        val tempFile = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)!!.use {
            tempBytes = it.readBytes()
        }

        FileOutputStream(tempFile).use {
            it.write(tempBytes)
        }
        return tempFile.absolutePath
    }

    fun removeTempFiles(context: Context) {
        File(context.cacheDir, "").deleteRecursively()
    }

    private fun getUriFileName(context: Context, uri: Uri): String? {
        // The query, because it only applies to a single document, returns only one row. There's no
        // need to filter, sort, or select fields, because we want all fields for one document.
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null, null)

        cursor?.use {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for "if there's
            // anything to look at, look at it" conditionals.
            if (it.moveToFirst()) {
                // Note it's called "Display Name". This is provider-specific, and might not
                // necessarily be the file name.
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index < 0) return null
                return it.getString(index)
            }
        }
        return null
    }

    fun getPathFileName(path: String) = path.split("/").last()
}
