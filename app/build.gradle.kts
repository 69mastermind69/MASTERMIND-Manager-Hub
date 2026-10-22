
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
}
