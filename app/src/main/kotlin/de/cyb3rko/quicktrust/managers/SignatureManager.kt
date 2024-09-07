/*
 * Copyright (c) 2023 Cyb3rKo
 * Inspired by MuntashirAkon/AppManager
 * https://github.com/MuntashirAkon/AppManager/blob/688308dcee755f24faa6cefd1a468891064a02e8/app/src/main/java/io/github/muntashirakon/AppManager/utils/PackageUtils.java
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
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.material.R
import com.google.android.material.color.MaterialColors
import de.cyb3rko.quicktrust.crypto.DigestUtils
import de.cyb3rko.quicktrust.signatures.HexEncoding
import de.cyb3rko.quicktrust.signatures.OidMap
import de.cyb3rko.quicktrust.signatures.SignerInfo
import java.io.ByteArrayInputStream
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateFactory
import java.security.cert.CertificateNotYetValidException
import java.security.cert.X509Certificate
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey

internal object SignatureManager {
    data class SigningCertificateInfo(
        val details: Spannable,
        val checksums: List<Pair<String, String>>
    )

    fun extractCertificatesFromApk(context: Context, path: String): List<X509Certificate>? {
        try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageArchiveInfo(
                    path,
                    PackageManager.PackageInfoFlags.of(
                        PackageManager.GET_SIGNING_CERTIFICATES.toLong()
                    )
                )?.signingInfo!!.signingCertificateHistory
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                extractSignaturesSub33(context, path)
            } else {
                extractSignaturesSub28(context, path)
            }
            return extractCertificatesFromSignatures(signatures.asList())
        } catch (e: Exception) {
            Log.w("QuickTrust", "Signature extraction failed")
            e.printStackTrace()
            return null
        }
    }

    fun extractCertificatesFromSignatures(signatures: List<Signature>): List<X509Certificate> {
        val certFactory = CertificateFactory.getInstance("X.509")
        val certificates = mutableListOf<X509Certificate>()

        signatures.forEach {
            certificates.add(
                certFactory.generateCertificate(
                    ByteArrayInputStream(it.toByteArray())
                ) as X509Certificate
            )
        }
        return certificates
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Suppress("DEPRECATION")
    private fun extractSignaturesSub33(context: Context, path: String): Array<Signature> {
        return context.packageManager.getPackageArchiveInfo(
            path,
            PackageManager.GET_SIGNING_CERTIFICATES
        )?.signingInfo!!.signingCertificateHistory
    }

    @Suppress("DEPRECATION")
    private fun extractSignaturesSub28(context: Context, path: String): Array<Signature> {
        return context.packageManager.getPackageArchiveInfo(
            path,
            PackageManager.GET_SIGNATURES
        )?.signatures!!
    }

    private val sSpannableFactory: Spannable.Factory = Spannable.Factory.getInstance()

    fun getSigningCertificateInfo(
        ctx: Context,
        certificate: X509Certificate?
    ): SigningCertificateInfo? {
        val builder = SpannableStringBuilder()
        if (certificate == null) return null
        val certBytes: ByteArray = certificate.encoded
        val checksums = DigestUtils.getDigests(certBytes)

        builder.append(getStyledKeyValue(ctx, "Subject", certificate.subjectX500Principal.name))
            .append("\n")
            .append(getStyledKeyValue(ctx, "Issuer", certificate.issuerX500Principal.name))
            .append("\n")
            .append(getStyledKeyValue(ctx, "Issued Date", certificate.notBefore.toString()))
            .append("\n")
            .append(getStyledKeyValue(ctx, "Expiry Date", certificate.notAfter.toString()))
            .append("\n")
            .append(getStyledKeyValue(ctx, "Type", certificate.type))
            .append(", ")
            .append(getStyledKeyValue(ctx, "Version", certificate.version.toString()))
            .append(", ")
        val validity: String = try {
            certificate.checkValidity()
            "Valid"
        } catch (e: CertificateExpiredException) {
            "Expired"
        } catch (e: CertificateNotYetValidException) {
            "Not yet valid"
        }
        builder.append(getStyledKeyValue(ctx, "Validity", validity))
            .append("\n")
            .append(getPrimaryText(ctx, "Serial number: "))
            .append(getMonospacedText(bytesToHex(certificate.serialNumber.toByteArray())))
            .append("\n")

        builder.append(getTitleText(ctx, "Checksums")).append("\n")
        for (pair in checksums) {
            builder.append(getPrimaryText(ctx, "${pair.first}: "))
                .append(getMonospacedText(pair.second))
                .append("\n")
        }

        builder.append(getTitleText(ctx, "Signature"))
            .append("\n")
            .append(getStyledKeyValue(ctx, "Algorithm", certificate.sigAlgName))
            .append("\n")
            .append(getStyledKeyValue(ctx, "OID", certificate.sigAlgOID))
            .append("\n")
            .append(getPrimaryText(ctx, "Signature: "))
            .append(getMonospacedText(bytesToHex(certificate.signature))).append("\n")
        // Public key used by Google: https://github.com/google/conscrypt
        // 1. X509PublicKey (PublicKey)
        // 2. OpenSSLRSAPublicKey (RSAPublicKey)
        // 3. OpenSSLECPublicKey (ECPublicKey)
        val publicKey = certificate.publicKey
        builder.append(getTitleText(ctx, "Public Key"))
            .append("\n")
            .append(getStyledKeyValue(ctx, "Algorithm", publicKey.algorithm))
            .append("\n")
            .append(getStyledKeyValue(ctx, "Format", publicKey.format))
        if (publicKey is RSAPublicKey) {
            builder.append("\n")
                .append(getStyledKeyValue(ctx, "RSA Exponent", publicKey.publicExponent.toString()))
                .append("\n")
                .append(getPrimaryText(ctx, "Modulus: "))
                .append(getMonospacedText(bytesToHex(publicKey.modulus.toByteArray())))
        } else if (publicKey is ECPublicKey) {
            builder.append("\n")
                .append(getStyledKeyValue(ctx, "DSA Affine X", publicKey.w.affineX.toString()))
                .append("\n")
                .append(getStyledKeyValue(ctx, "DSA Affine Y", publicKey.w.affineY.toString()))
        }

        val critSet: Set<String>? = certificate.criticalExtensionOIDs
        if (!critSet.isNullOrEmpty()) {
            builder.append("\n").append(getTitleText(ctx, "Critical extensions"))
            for (oid in critSet) {
                val oidName: String = OidMap.getName(oid)
                builder.append("\n- ")
                    .append(getPrimaryText(ctx, "$oidName: "))
                    .append(getMonospacedText(bytesToHex(certificate.getExtensionValue(oid))))
            }
        }
        val nonCritSet: Set<String>? = certificate.nonCriticalExtensionOIDs
        if (!nonCritSet.isNullOrEmpty()) {
            builder.append("\n")
                .append(getTitleText(ctx, "Non-critical extensions"))
            for (oid in nonCritSet) {
                val oidName: String = OidMap.getName(oid)
                builder.append("\n- ")
                    .append(getPrimaryText(ctx, "$oidName: "))
                    .append(getMonospacedText(bytesToHex(certificate.getExtensionValue(oid))))
            }
        }
        return SigningCertificateInfo(builder, checksums)
    }

    private fun getStyledKeyValue(
        context: Context,
        key: CharSequence?,
        value: CharSequence?
    ): Spannable {
        return SpannableStringBuilder(getPrimaryText(context, "$key: ")).append(value)
    }

    private fun getPrimaryText(context: Context, text: CharSequence): Spannable {
        return getColoredText(
            setTypefaceSpan(text, "sans-serif-medium"),
            getTextColorPrimary(context)
        )
    }

    private fun setTypefaceSpan(text: CharSequence, family: String): Spannable {
        val spannable = charSequenceToSpannable(text)
        spannable.setSpan(
            TypefaceSpan(family),
            0,
            spannable.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun getTextColorPrimary(context: Context): Int {
        return MaterialColors.getColor(context, R.attr.colorOnSurface, -1)
    }

    private fun getColoredText(text: CharSequence, color: Int): Spannable {
        val spannable: Spannable = charSequenceToSpannable(text)
        spannable.setSpan(
            ForegroundColorSpan(color),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun charSequenceToSpannable(text: CharSequence): Spannable {
        return if (text is Spannable) {
            text
        } else {
            sSpannableFactory.newSpannable(text)
        }
    }

    private fun getMonospacedText(text: CharSequence): Spannable {
        return setTypefaceSpan(text, "monospace")
    }

    private fun getTitleText(context: Context, text: CharSequence): Spannable {
        val spannable = charSequenceToSpannable(text)
        spannable.setSpan(
            AbsoluteSizeSpan(40),
            0,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return getPrimaryText(context, spannable)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return HexEncoding.encodeToString(bytes, false)
    }

    private fun getSigningInfo(packageInfo: PackageInfo): Array<Signature?>? {
        val signerInfo: SignerInfo = getSignerInfo(packageInfo) ?: return null
        return signerInfo.getSignatures()
    }

    private fun getSignerInfo(packageInfo: PackageInfo): SignerInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = packageInfo.signingInfo
                return if (signingInfo == null) {
                    null
                } else {
                    SignerInfo(signingInfo)
                }
            }
        }
        return SignerInfo(packageInfo.signatures)
    }

    fun getIssuerAndAlg(p: PackageInfo): Pair<String, String> {
        val signatures: Array<Signature?>? = getSigningInfo(p)
        var certificate: X509Certificate
        if (signatures == null) return Pair("", "")
        var name = ""
        var algoName = ""
        signatures.forEach {
            try {
                if (it != null) {
                    ByteArrayInputStream(it.toByteArray()).use { `is` ->
                        certificate = CertificateFactory.getInstance("X.509")
                            .generateCertificate(`is`) as X509Certificate
                        name = certificate.issuerX500Principal.name
                        algoName = certificate.sigAlgName
                        return@forEach
                    }
                }
            } catch (ignore: Exception) {
            }
        }
        return Pair(name, algoName)
    }

    fun getPackageSignatures(context: Context, packageName: String): List<Signature> {
        val packageManager = context.packageManager
        val flagSigningInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(flagSigningInfo.toLong())
            ).signingInfo?.signingCertificateHistory?.asList() ?: listOf()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(
                packageName,
                flagSigningInfo
            ).signingInfo?.signingCertificateHistory?.asList() ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, flagSigningInfo)
                .signatures?.asList() ?: listOf()
        }
    }
}
