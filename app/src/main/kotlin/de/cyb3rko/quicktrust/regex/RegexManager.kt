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

package de.cyb3rko.quicktrust.regex

internal object RegexManager {
    enum class Pattern(val value: String) {
        SHA512("(?:[a-f0-9]{2}:?){64}"),
        SHA384("(?:[a-f0-9]{2}:?){48}"),
        SHA256("(?:[a-f0-9]{2}:?){32}"),
        SHA224("(?:[a-f0-9]{2}:?){28}"),
        SHA1("(?:[a-f0-9]{2}:?){20}"),
        MD5("(?:[a-f0-9]{2}:?){16}")
    }

    fun match(input: String): RegexMatch? {
        var regex: Regex
        Pattern.values().forEach { pattern ->
            regex = Regex(pattern.value, setOf(RegexOption.IGNORE_CASE))
            regex.find(input)?.let { result ->
                return RegexMatch(pattern, result.value.replace(":", ""))
            }
        }
        return null
    }
}
