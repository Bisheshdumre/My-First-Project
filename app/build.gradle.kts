plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.finalproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.finalproject"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        viewBinding = true
    }

    // Improves kapt error messages
    kapt {
        correctErrorTypes = true
    }

    // ✅ Important for Robolectric to work with resources
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // --- Core Android dependencies ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- Hilt for Dependency Injection ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // --- Retrofit for API calls ---
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)

    // --- Default test libraries ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- Extra libraries for unit testing ---

    // Google Truth for clean assertions
    testImplementation("com.google.truth:truth:1.4.5")

    // Robolectric for UI component testing without a device/emulator
    testImplementation("org.robolectric:robolectric:4.16")
    testImplementation("androidx.test:core:1.7.0")

    // Retrofit/OkHttp MockWebServer for API endpoint simulation
    testImplementation("com.squareup.okhttp3:mockwebserver:5.1.0")
    testImplementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Mockito for mocking if needed (optional but useful)
    testImplementation("org.mockito:mockito-core:5.19.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.0.0")
}
