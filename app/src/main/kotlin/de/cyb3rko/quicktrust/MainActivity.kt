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

package de.cyb3rko.quicktrust

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import de.cyb3rko.quicktrust.databinding.ActivityMainBinding
import de.cyb3rko.quicktrust.managers.SignatureManager
import de.cyb3rko.quicktrust.managers.StorageManager
import de.cyb3rko.quicktrust.modals.AboutDialog
import de.cyb3rko.quicktrust.modals.BuildInfo
import de.cyb3rko.quicktrust.modals.PackagesBottomSheet
import de.cyb3rko.quicktrust.regex.RegexManager
import de.cyb3rko.quicktrust.regex.RegexMatch
import de.cyb3rko.quicktrust.utils.ExceptionHandler
import de.cyb3rko.quicktrust.utils.openUrl
import java.io.FileInputStream
import java.security.cert.X509Certificate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var signingCertificateInfo: SignatureManager.SigningCertificateInfo? = null

    private val apkPickerResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val apkUri = result.data?.data ?: return@registerForActivityResult
                processApkUri(apkUri)
            }
        }

    private val checksumPickerResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                processChecksumUri(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.checkInput.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank()) {
                recognizeInput(text.toString())
            } else {
                binding.resultCard.visibility = View.GONE
                binding.checkInputLayout.helperText = null
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        binding.apkCard.setOnClickListener {
            StorageManager.launchFileSelector(apkPickerResultLauncher)
        }

        binding.packagesCard.setOnClickListener {
            PackagesBottomSheet {
                binding.toolbar.subtitle = getString(R.string.toolbar_subtitle_app, it.label)
                lifecycleScope.launch(Dispatchers.IO) {
                    val signatures = SignatureManager.getPackageSignatures(
                        this@MainActivity,
                        it.packageName
                    )
                    val certificates = SignatureManager.extractCertificatesFromSignatures(
                        signatures
                    )
                    processCertificates(certificates)
                }
            }.show(supportFragmentManager, PackagesBottomSheet.TAG)
        }

        binding.hashFileButton.setOnClickListener {
            StorageManager.launchFileSelector(checksumPickerResultLauncher)
        }

        StorageManager.removeTempFiles(this)
    }

    private fun processApkUri(uri: Uri) {
        binding.resultCard.visibility = View.GONE
        lifecycleScope.launch(Dispatchers.IO) {
            val path = StorageManager.copyTempFile(this@MainActivity, uri, true)
            if (path == null) {
                StorageManager.removeTempFiles(this@MainActivity)
                withContext(Dispatchers.Main) {
                    showResult(
                        false,
                        R.string.verification_failure,
                        R.string.verification_wrong_file_type_desc
                    )
                    binding.toolbar.subtitle = null
                }
                return@launch
            }
            val certificates = SignatureManager.extractCertificatesFromApk(this@MainActivity, path)
            StorageManager.removeTempFiles(this@MainActivity)
            withContext(Dispatchers.Main) {
                binding.toolbar.subtitle = getString(
                    R.string.toolbar_subtitle_file,
                    StorageManager.getPathFileName(path)
                )
            }
            processCertificates(certificates)
        }
    }

    private fun processChecksumUri(uri: Uri) {
        binding.resultCard.visibility = View.GONE
        val path = StorageManager.copyTempFile(this, uri, false)
        FileInputStream(path).use {
            val text = it.readBytes().decodeToString()
            val regexMatch = RegexManager.match(text)
            if (regexMatch != null) {
                binding.checkInput.setText(regexMatch.match)
            } else {
                Toast.makeText(this, R.string.hash_import_no_hash, Toast.LENGTH_SHORT).show()
            }
        }
        StorageManager.removeTempFiles(this)
    }

    private fun recognizeInput(input: String) {
        val regexMatch = RegexManager.match(input.replace("\n", ""))
        if (regexMatch != null) {
            binding.checkInputLayout.helperText = getString(
                R.string.check_input_found,
                regexMatch.pattern,
                regexMatch.match
            )
            validateChecksum(regexMatch)
        } else {
            binding.resultCard.visibility = View.GONE
            binding.checkInputLayout.helperText = getString(R.string.check_input_no_hash)
        }
    }

    private suspend fun processCertificates(certificates: List<X509Certificate>?) {
        if (certificates != null) {
            val info = SignatureManager.getSigningCertificateInfo(this, certificates[0])
            info?.let {
                withContext(Dispatchers.Main) {
                    binding.resultCard.visibility = View.GONE
                    binding.contentView.text = info.details
                    signingCertificateInfo = info
                    val input = binding.checkInput.text.toString()
                    if (input.isNotBlank()) {
                        recognizeInput(binding.checkInput.text.toString())
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                showResult(
                    false,
                    R.string.verification_failure,
                    R.string.verification_extraction_failure_desc
                )
            }
        }
    }

    private fun validateChecksum(regexMatch: RegexMatch) {
        var found = false
        if (signingCertificateInfo != null) {
            signingCertificateInfo!!.checksums.forEach {
                if (it.second == regexMatch.match.lowercase()) {
                    showResult(true)
                    found = true
                }
            }
            if (!found) {
                showResult(false, R.string.verification_invalid)
            }
        } else {
            binding.resultCard.visibility = View.GONE
        }
    }

    private fun showResult(
        success: Boolean,
        @StringRes result: Int? = null,
        @StringRes description: Int? = null
    ) {
        if (success) {
            binding.resultText.text = getString(R.string.verification_pass)
            binding.resultCard.setCardColor(R.color.md_theme_dark_primaryContainer)
            binding.resultCard.setCardStrokeColor(R.color.md_theme_dark_primaryContainer)
            binding.resultCard.visibility = View.VISIBLE
        } else {
            binding.resultText.text = getString(result!!)
            binding.resultCard.setCardColor(R.color.md_theme_dark_errorContainer)
            binding.resultCard.setCardStrokeColor(R.color.md_theme_dark_errorContainer)
            binding.resultCard.visibility = View.VISIBLE
            description?.let {
                binding.contentView.text = getString(it)
            }
        }
    }

    private fun MaterialCardView.setCardColor(@ColorRes color: Int) {
        setCardBackgroundColor(
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                resources.getColor(color, theme)
            } else {
                @Suppress("DEPRECATION")
                resources.getColor(color)
            }
        )
    }

    private fun MaterialCardView.setCardStrokeColor(@ColorRes color: Int) {
        strokeColor = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            resources.getColor(color, theme)
        } else {
            @Suppress("DEPRECATION")
            resources.getColor(color)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_git -> {
            openUrl("https://github.com/cyb3rko/quicktrust", "GitHub Repo")
            true
        }
        R.id.action_about -> {
            AboutDialog.show(
                this,
                BuildInfo(
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    BuildConfig.BUILD_TYPE
                )
            )
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
