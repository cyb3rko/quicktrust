import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jmailen.kotlinter") version "4.4.1" // lintKotlin, formatKotlin
    id("com.getkeepsafe.dexcount") version "4.0.0" // :app:countReleaseDexMethods
}

android {
    namespace = "de.cyb3rko.quicktrust"
    compileSdk = 35
    defaultConfig {
        applicationId = "de.cyb3rko.quicktrust"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        resourceConfigurations.add("en")
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    bundle {
        storeArchive {
            enable = false
        }
    }
    packaging {
        resources {
            excludes.add("META-INF/*.version")
            excludes.add("META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")
    implementation("com.google.android.material:material:1.12.0")
}

if (project.hasProperty("sign")) {
    android {
        signingConfigs {
            getByName("release") {
                storeFile = file(System.getenv("KEYSTORE_FILE"))
                storePassword = System.getenv("KEYSTORE_PASSWD")
                keyAlias = System.getenv("KEYSTORE_KEY_ALIAS")
                keyPassword = System.getenv("KEYSTORE_KEY_PASSWD")
            }
        }
    }
    android.buildTypes.getByName("release").signingConfig =
        android.signingConfigs.getByName("release")
}

if (project.hasProperty("gplay_upload")) {
    android {
        signingConfigs {
            getByName("upload") {
                val properties = Properties()
                properties.load(project.rootProject.file("local.properties").inputStream())
                storeFile = file(properties.getProperty("uploadsigning.file"))
                storePassword = properties.getProperty("uploadsigning.password")
                keyAlias = properties.getProperty("uploadsigning.key.alias")
                keyPassword = properties.getProperty("uploadsigning.key.password")
            }
        }
    }
    android.buildTypes.getByName("release").signingConfig =
        android.signingConfigs.getByName("upload")
}