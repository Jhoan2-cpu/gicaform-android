plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.gica2025"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gica2025"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Configuración del servidor por defecto
        buildConfigField("String", "BASE_URL", "\"https://solid-plot.localsite.io/\"")
        buildConfigField("String", "API_PATH", "\"wp-json/gicaform/v1/\"")
        buildConfigField("String", "FCM_ENDPOINT", "\"wp-content/plugins/GICAACCOUNT/mobile-api-public.php\"")
        buildConfigField("String", "HTTP_USERNAME", "\"cushion\"")
        buildConfigField("String", "HTTP_PASSWORD", "\"neighborly\"")
        buildConfigField("String", "FCM_API_KEY", "\"gica_mobile_2024\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Configuración para desarrollo/testing (servidor actual)
            buildConfigField("String", "BASE_URL", "\"https://solid-plot.localsite.io/\"")
            buildConfigField("String", "HTTP_USERNAME", "\"cushion\"")
            buildConfigField("String", "HTTP_PASSWORD", "\"neighborly\"")
        }
    }
    
    // Build variants para diferentes servidores
    flavorDimensions += "server"
    productFlavors {
        create("production") {
            dimension = "server"
            // Configuración para servidor de producción
            buildConfigField("String", "BASE_URL", "\"https://solid-plot.localsite.io/\"")
            buildConfigField("String", "HTTP_USERNAME", "\"cushion\"")
            buildConfigField("String", "HTTP_PASSWORD", "\"neighborly\"")
        }
        create("staging") {
            dimension = "server"
            // Configuración para servidor de staging
            buildConfigField("String", "BASE_URL", "\"https://solid-plot.localsite.io/\"")
            buildConfigField("String", "HTTP_USERNAME", "\"cushion\"")
            buildConfigField("String", "HTTP_PASSWORD", "\"neighborly\"")
        }
        create("development") {
            dimension = "server"
            // Configuración actual (servidor actual)
            buildConfigField("String", "BASE_URL", "\"https://solid-plot.localsite.io/\"")
            buildConfigField("String", "HTTP_USERNAME", "\"cushion\"")
            buildConfigField("String", "HTTP_PASSWORD", "\"neighborly\"")
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
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Firebase BOM para manejar versiones automáticamente
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.compose.activity)
    
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // Splash Screen
    implementation(libs.androidx.core.splashscreen)
    
    // Animations
    implementation(libs.lottie.compose)
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}