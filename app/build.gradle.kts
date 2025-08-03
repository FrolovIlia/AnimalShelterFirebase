plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.gms)
    alias(libs.plugins.plugin.serialization)
}

android {
    namespace = "com.pixelrabbit.animalshelterfirebase"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pixelrabbit.animalshelterfirebase"
        minSdk = 24
        targetSdk = 35
        versionCode = 6
        versionName = "1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlin.serialization.json)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.coil.compose)
    implementation(libs.firebase.auth)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.webkit)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.ui.tooling)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.material.icons.extended)

// Compose Material3 (гарантирует доступ к TextField, Text, LocalTextStyle и т.д.)
    implementation("androidx.compose.material3:material3:1.2.1")

// Compose UI (включает text input types и другие компоненты UI)
    implementation("androidx.compose.ui:ui:1.6.0")

// Compose tooling (для превью и тестов, уже есть у тебя)
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")

// Material Icons (уже есть — отлично)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

//Для уведомлений
    implementation(libs.firebase.messaging.ktx)

    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.mobileads)

    // Для слайд-шоу
    implementation(libs.androidx.foundation.v160)


}