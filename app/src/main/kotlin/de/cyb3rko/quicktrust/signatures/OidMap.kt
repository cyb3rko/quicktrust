/*
 * Copyright (c) 2023-2024 Cyb3rKo
 * Inspired by MuntashirAkon/AppManager:
 * https://github.com/MuntashirAkon/AppManager/blob/688308dcee755f24faa6cefd1a468891064a02e8/app/src/main/java/io/github/muntashirakon/AppManager/misc/OidMap.java
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

internal object OidMap {
    private val oidNameMap = hashMapOf(
        "2.5.29.9" to "subjectDirectoryAttributes",
        "2.5.29.14" to "subjectKeyIdentifier",
        "2.5.29.15" to "keyUsage",
        "2.5.29.16" to "privateKeyUsagePeriod",
        "2.5.29.17" to "subjectAltName",
        "2.5.29.18" to "issuerAltName",
        "2.5.29.19" to "basicConstraints",
        "2.5.29.20" to "cRLNumber",
        "2.5.29.21" to "reasonCode",
        "2.5.29.23" to "instructionCode",
        "2.5.29.24" to "invalidityDate",
        "2.5.29.27" to "deltaCRLIndicator",
        "2.5.29.28" to "issuingDistributionPoint",
        "2.5.29.29" to "certificateIssuer",
        "2.5.29.30" to "nameConstraints",
        "2.5.29.31" to "cRLDistributionPoints",
        "2.5.29.32" to "certificatePolicies",
        "2.5.29.33" to "policyMappings",
        "2.5.29.35" to "authorityKeyIdentifier",
        "2.5.29.36" to "policyConstraints",
        "2.5.29.37" to "extKeyUsage",
        "2.5.29.38" to "authorityAttributeIdentifier",
        "2.5.29.39" to "roleSpecCertIdentifier",
        "2.5.29.40" to "cRLStreamIdentifier",
        "2.5.29.41" to "basicAttConstraints",
        "2.5.29.42" to "delegatedNameConstraints",
        "2.5.29.43" to "timeSpecification",
        "2.5.29.44" to "cRLScope",
        "2.5.29.45" to "statusReferrals",
        "2.5.29.46" to "freshestCRL",
        "2.5.29.47" to "orderedList",
        "2.5.29.48" to "attributeDescriptor",
        "2.5.29.49" to "userNotice",
        "2.5.29.50" to "sOAIdentifier",
        "2.5.29.51" to "baseUpdateTime",
        "2.5.29.52" to "acceptableCertPolicies",
        "2.5.29.53" to "deltaInfo",
        "2.5.29.54" to "inhibitAnyPolicy",
        "2.5.29.55" to "targetInformation",
        "2.5.29.56" to "noRevAvail",
        "2.5.29.57" to "acceptablePrivilegePolicies",
        "2.5.29.61" to "indirectIssuer",
        "1.3.6.1.5.5.7.1.1" to "AuthorityInfoAccess",
        "1.3.6.1.5.5.7.1.11" to "SubjectInfoAccess",
        "1.3.6.1.5.5.7.48.1.5" to "OCSPNoCheck"
    )

    fun getName(oid: String): String {
        return oidNameMap[oid]!!
    }
}
