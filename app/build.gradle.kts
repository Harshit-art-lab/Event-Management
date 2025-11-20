plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.eventmanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eventmanagement"
        minSdk = 24
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.15.0")  // Updated
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")  // Updated
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")  // Updated
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // DrawerLayout
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")  // Updated
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")  // Updated
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")  // Match version

    // QR Code
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")  // Updated
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")  // Updated
}