/*
 * Copyright (c) 2023-2024 Cyb3rKo
 * Inspired by MuntashirAkon/AppManager:
 * https://github.com/MuntashirAkon/AppManager/blob/688308dcee755f24faa6cefd1a468891064a02e8/app/src/main/java/io/github/muntashirakon/AppManager/apk/signing/SignerInfo.java
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

package de.cyb3rko.quicktrust.signatures

import android.content.pm.Signature
import android.content.pm.SigningInfo
import android.os.Build
import androidx.annotation.RequiresApi

internal class SignerInfo {
    private val currentSignatures: Array<Signature?>?

    @RequiresApi(Build.VERSION_CODES.P)
    constructor(signingInfo: SigningInfo?) {
        if (signingInfo == null) {
            currentSignatures = null
            return
        }
        currentSignatures = signingInfo.apkContentsSigners
    }

    constructor(signatures: Array<Signature?>?) {
        currentSignatures = signatures
    }

    fun getSignatures(): Array<Signature?>? {
        return currentSignatures
    }
}
