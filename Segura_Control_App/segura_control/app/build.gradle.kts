plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")


    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.segura_control"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.segura_control"
        minSdk = 27
        targetSdk = 35
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // Configuración de kapt para Room
    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
}

dependencies {

    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Google Play Services
    implementation(libs.play.services.location)

    // Room (Base de Datos)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.transport.api)
    implementation(libs.firebase.firestore.ktx)
    kapt(libs.androidx.room.compiler.v272) // Procesador de anotaciones para Room con KAPT

    // UI/Recycler View
    implementation(libs.androidx.recyclerview)

    // LiveData y ViewModel (Arquitectura Android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core) // Añadir kotlinx-coroutines-core

    // Retrofit para API REST (Imgur)
    implementation(libs.retrofit)
    implementation(libs.gson) // Librería Gson para serialización/deserialización
    implementation(libs.converter.gson) // Conversor de Gson para Retrofit
    implementation(libs.okhttp) // Cliente HTTP principal para Retrofit
    implementation(libs.okhttp.logging.interceptor) // Interceptor de logs para OkHttp
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)


    implementation(libs.androidx.exifinterface)
    implementation(libs.play.services.location)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging.ktx)


}
