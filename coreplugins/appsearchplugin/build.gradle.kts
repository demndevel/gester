plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.demn.appsearchplugin"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation(project(":plugincore"))
    implementation(project(":domain"))
    implementation(project(":coreplugins:base"))

    api(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(project(":domain"))
    ksp(libs.room.compiler)
    implementation(libs.fuzzywuzzy.kotlin)
    implementation(libs.androidx.core.ktx)
    implementation(libs.latin.to.cyrillic)

    implementation(libs.koin.core)

    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}