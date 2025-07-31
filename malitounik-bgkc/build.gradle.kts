import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "by.carkva_gazeta.malitounik"
    compileSdk = 36

    defaultConfig {
        applicationId = "by.carkva_gazeta.malitounik"
        minSdk = 23
        targetSdk = 36
        versionCode = 442522
        versionName = "5.99.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    buildFeatures {
        viewBinding = false
        buildConfig = true
        compose = true
    }
    bundle {
        density.enableSplit = true
        language.enableSplit = false
        abi.enableSplit = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    dynamicFeatures += setOf(":admin")
}

dependencies {
    api(libs.androidx.fragment.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    api(platform(libs.firebase.bom))
    api(libs.play.services.instantapps)
    api(libs.gson)
    api(libs.firebase.storage)
    implementation(libs.feature.delivery.ktx)
    implementation(libs.app.update.ktx)
    implementation (libs.androidx.media3.exoplayer)
    implementation (libs.androidx.media3.exoplayer.smoothstreaming)
    implementation (libs.androidx.media)
    //implementation(libs.androidx.documentfile)
    implementation(libs.androidx.paging.compose)
    implementation(libs.coil3)
    implementation(libs.androidx.glance.appwidget)
    //implementation(libs.jsoup)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

apply(plugin = "com.google.gms.google-services")
