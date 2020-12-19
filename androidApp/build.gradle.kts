plugins {
    id("com.android.application")
    kotlin("android")
}

val compose_version = "1.0.0-alpha08"
val kotlin_version = "1.4.20"

dependencies {
    implementation(project(":shared"))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.compose.compiler:compiler:$compose_version")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.ui:ui-tooling:$compose_version")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.owlsoft.turntoroll.androidApp"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerVersion = kotlin_version
        kotlinCompilerExtensionVersion = compose_version
    }
}