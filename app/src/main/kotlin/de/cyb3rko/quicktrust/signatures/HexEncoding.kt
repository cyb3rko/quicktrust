/*
 * Copyright (c) 2023-2024 Cyb3rKo
 * Inspired by MuntashirAkon/AppManager:
 * https://github.com/MuntashirAkon/AppManager/blob/688308dcee755f24faa6cefd1a468891064a02e8/app/src/main/java/aosp/libcore/util/HexEncoding.java
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

internal object HexEncoding {
    private const val LOWER_CASE_DIGITS = "0123456789abcdef"
    private const val UPPER_CASE_DIGITS = "0123456789ABCDEF"

    private fun encode(data: ByteArray, upperCase: Boolean): CharArray {
        return encode(data, data.size, upperCase)
    }

    private fun encode(data: ByteArray, len: Int, upperCase: Boolean): CharArray {
        val digits = if (upperCase) UPPER_CASE_DIGITS else LOWER_CASE_DIGITS
        val result = CharArray(len * 2)
        for (i in 0 until len) {
            val b = data[i]
            val resultIndex = 2 * i
            result[resultIndex] = digits[b.toInt() shr 4 and 0x0f]
            result[resultIndex + 1] = digits[b.toInt() and 0x0f]
        }
        return result
    }

    fun encodeToString(data: ByteArray, upperCase: Boolean): String {
        return String(encode(data, upperCase))
    }
}
