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
        versionCode = 8
        versionName = "1.7"
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

    flavorDimensions += "shelter"
    productFlavors {
        create("shelter1") {
            applicationId = "com.pixelrabbit.animalshelterfirebase"
            dimension = "shelter"

            resValue("string", "app_name", "Майский День")

            // 6 рекламных блоков Яндекс
            resValue("string", "yandex_card_open_id", "R-M-16111641-8")
            resValue("string", "yandex_banner_vertical_id", "R-M-16111641-5")
            resValue("string", "yandex_banner_horizontal_id", "R-M-16111641-4")
            resValue("string", "yandex_task_banner_id", "R-M-16111641-3")
            resValue("string", "yandex_donate_banner_id", "R-M-16111641-2")
            resValue("string", "yandex_interstitial_id", "R-M-16111641-1")

            // manifest placeholder для Yandex APP_ID
            manifestPlaceholders["YandexAppId"] = "16111641"
        }
        create("shelter2") {
            applicationId = "com.pixelrabbit.shelter2"
            dimension = "shelter"

            resValue("string", "app_name", "Приют 2")

            resValue("string", "yandex_card_open_id", "stub-8")
            resValue("string", "yandex_banner_vertical_id", "stub-5")
            resValue("string", "yandex_banner_horizontal_id", "stub-4")
            resValue("string", "yandex_task_banner_id", "stub-3")
            resValue("string", "yandex_donate_banner_id", "stub-2")
            resValue("string", "yandex_interstitial_id", "stub-1")

            manifestPlaceholders["YandexAppId"] = "NEW_APP_ID_FOR_SHELTER2"
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.foundation)
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
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.mobileads)
    implementation(libs.androidx.foundation.v160)
    implementation(libs.accompanist.systemuicontroller.v0340)
}
