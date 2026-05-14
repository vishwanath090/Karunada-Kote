import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val localProperties = Properties()

val localPropertiesFile =
    rootProject.file("local.properties")

if (localPropertiesFile.exists()) {

    localProperties.load(
        localPropertiesFile.inputStream()
    )
}

android {

    namespace = "com.karunadakote"

    compileSdk = 34

    defaultConfig {

        applicationId = "com.karunadakote"

        minSdk = 26

        targetSdk = 34

        versionCode = 1

        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        // Gemini API Key
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY")}\""
        )
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {

        buildConfig = true

        viewBinding = true
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {

        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.appcompat)

    implementation(libs.material)

    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.ktx)

    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.recyclerview)

    implementation(libs.retrofit)

    implementation(libs.retrofit.gson)

    implementation(libs.okhttp)

    implementation(libs.okhttp.logging)

    implementation(libs.gson)

    implementation(libs.coroutines.android)

    // OpenStreetMap
    implementation(
        "org.osmdroid:osmdroid-android:6.1.18"
    )

    // Shimmer Loading
    implementation(
        "com.facebook.shimmer:shimmer:0.5.0"
    )

    // Swipe Refresh
    implementation(
        "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    )
    implementation("io.coil-kt:coil:2.6.0")
}