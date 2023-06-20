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

package de.cyb3rko.quicktrust.utils

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import de.cyb3rko.quicktrust.MainActivity
import de.cyb3rko.quicktrust.R
import de.cyb3rko.quicktrust.databinding.ActivityUncaughtExceptionBinding

internal class UncaughtExceptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUncaughtExceptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)

        binding = ActivityUncaughtExceptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val stacktrace = intent.getStringExtra(ExceptionHandler.EXTRA_STACKTRACE) ?: "no stacktrace"
        Log.d("UncaughtException", stacktrace)
        binding.stracktraceView.text = stacktrace
        binding.copyButton.setOnClickListener {
            storeToClipboard("Stacktrace", stacktrace)
            showToast(getString(R.string.toast_stacktrace), Toast.LENGTH_LONG)
        }
        binding.reportButton.setOnClickListener {
            openUrl("https://github.com/cyb3rko/quicktrust/issues", "GitHub Issues")
        }
    }
}
