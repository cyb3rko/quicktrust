/*
 * Copyright (c) 2023-2024 Cyb3rKo
 * Inspired by MuntashirAkon/AppManager:
 * https://github.com/MuntashirAkon/AppManager/blob/688308dcee755f24faa6cefd1a468891064a02e8/app/src/main/java/io/github/muntashirakon/AppManager/utils/DigestUtils.java
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

package de.cyb3rko.quicktrust.crypto

import de.cyb3rko.quicktrust.signatures.HexEncoding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

internal object DigestUtils {
    enum class Algorithm(val identifier: String) {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA224("SHA-224"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512")
    }

    private fun getHexDigest(algo: String, bytes: ByteArray): String =
        HexEncoding.encodeToString(getDigest(algo, bytes), false)

    private fun getDigest(algo: String, bytes: ByteArray): ByteArray = try {
        MessageDigest.getInstance(algo).digest(bytes)
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        ByteArray(0)
    }

    fun getDigests(bytes: ByteArray): List<Pair<String, String>> {
        val digests = mutableListOf<Pair<String, String>>()
        Algorithm.values().forEach { algo ->
            digests.add(
                Pair(algo.name, getHexDigest(algo.identifier, bytes))
            )
        }
        return digests
    }
}
