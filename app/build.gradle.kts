plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.sickcare_application"
    compileSdk = 35  // Mise à jour de compileSdk à 35

    defaultConfig {
        applicationId = "com.example.sickcare_application"
        minSdk = 24
        targetSdk = 35  // Mise à jour de targetSdk à 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // OkHttp pour la gestion des requêtes réseau
    implementation(libs.okhttp)
    implementation (libs.java.jwt)

    // Retrofit pour les appels API
    implementation(libs.retrofit2.retrofit)

    // Gson converter pour Retrofit
    implementation(libs.converter.gson)

    // Autres dépendances
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    implementation(libs.core.ktx)
    implementation (libs.recyclerview)
    implementation (libs.picasso)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
