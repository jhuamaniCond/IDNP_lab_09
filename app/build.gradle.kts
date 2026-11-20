plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // KSP aplicado al módulo :app para que procese las anotaciones de Room
    // (@Entity, @Dao, @Database) y genere las clases de implementación.
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.idnp_lab_09"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.idnp_lab_09"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ── Room ──────────────────────────────────────────────────────────────────
    // room-runtime: clases principales (RoomDatabase, Room.databaseBuilder, etc.)
    implementation(libs.androidx.room.runtime)
    // room-ktx: extensiones Kotlin que habilitan suspend fun y Flow en los DAOs
    implementation(libs.androidx.room.ktx)
    // room-compiler (ksp): genera en tiempo de compilación DocumentoDao_Impl y
    // CampusDocsDatabase_Impl; debe declararse con ksp() en lugar de kapt().
    ksp(libs.androidx.room.compiler)
}