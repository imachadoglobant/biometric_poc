plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.protobuf")
}

android {
    namespace = "com.sample.biometric"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sample.biometric"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //Timber logger
    implementation("com.jakewharton.timber:timber:5.0.1")
    // core ktx
    implementation("androidx.core:core-ktx:1.13.1")
    //lifecycle
    implementation( "androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    //compose
    implementation("androidx.activity:activity-compose:1.3.0")
    implementation("androidx.compose.ui:ui:1.7.6")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.6")
    implementation("androidx.compose.material:material:1.7.6")
    implementation("androidx.compose.material:material-icons-extended:1.7.7")
    // compose + viewmodel & lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    // Navigation compose
    implementation("androidx.navigation:navigation-compose:2.6.0")
    // Dagger 2
    implementation ("com.google.dagger:dagger:2.43.2")
    implementation ("com.google.dagger:dagger-android:2.43.2")
    annotationProcessor("com.google.dagger:dagger-compiler:2.43.2")
    kapt("com.google.dagger:dagger-compiler:2.43.2")
    kapt("com.google.dagger:dagger-android-processor:2.43.2")
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.2")
    implementation("androidx.datastore:datastore:1.1.2")
    implementation("com.google.protobuf:protobuf-javalite:4.29.3")
    implementation("com.google.protobuf:protobuf-kotlin-lite:4.29.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    // Biometric
    implementation("androidx.biometric:biometric:1.1.0")
    //Test & dev support
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.8")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.8")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.3"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins{
                create("java") {
                    option("lite")
                }
            }
        }
    }
}