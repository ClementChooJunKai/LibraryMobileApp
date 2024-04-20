plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.team10mobileproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.team10mobileproject"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.5")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.camera:camera-view:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.5.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation ("com.github.afreakyelf:Pdf-Viewer:2.0.7")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.5")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")


    testImplementation ("androidx.test:core:1.5.0")
// Jetpack Compose
    implementation ("androidx.compose.ui:ui:1.6.5") // Use the latest version
    implementation ("androidx.compose.material:material:1.6.5") // Use the latest version
    implementation ("androidx.compose.ui:ui-tooling:1.6.5") // Use the latest version
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.5.1")
    testImplementation ("org.mockito:mockito-inline:3.12.4") // For mocking final classes
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4")
    debugImplementation ("androidx.compose.ui:ui-tooling")
    debugImplementation ("androidx.compose.ui:ui-test-manifest")
// Accompanist Permissions
    implementation ("com.google.accompanist:accompanist-permissions:0.34.0")

    implementation ("androidx.camera:camera-core:1.3.2")
    // CameraX Camera2 implementation
    implementation ("androidx.camera:camera-camera2:1.3.2")
    // CameraX Lifecycle library
    implementation ("androidx.camera:camera-lifecycle:1.3.2")
    // CameraX View class
    implementation ("androidx.camera:camera-view:1.3.2")
    // CameraX Extensions
    implementation ("androidx.camera:camera-extensions:1.3.2")
    implementation ("androidx.work:work-runtime-ktx:2.9.0") // Check for the latest version
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")

}