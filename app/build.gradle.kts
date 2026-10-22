
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mastermind.managerhub"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mastermind.managerhub"

        minSdk = 26
        targetSdk = 35

        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Android AppCompat
    implementation("androidx.appcompat:appcompat:1.7.1")

    // AndroidX Activity Result API
    implementation("androidx.activity:activity-ktx:1.10.1")

    // AndroidX Core utilities
    implementation("androidx.core:core-ktx:1.16.0")
}
