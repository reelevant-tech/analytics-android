plugins {
    id("maven-publish")
    id("com.android.library")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.reelevant.analytics_android"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.io.mockk.mockk2)
    testImplementation(libs.json)
    androidTestImplementation(libs.io.mockk.mockk.android)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(kotlin("reflect"))
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            run {
                groupId = "com.reelevant.analytics"
                artifactId = "analytics-android-release"
                version = "0.0.1-SNAPSHOT"
                artifact(layout.buildDirectory.file("outputs/aar/analytics-android-release.aar")).toString()
            }
        }
    }
    repositories {
        maven {
            name = "ReelevantAndroidAnalytics"
            url = uri("https://maven.pkg.github.com/reelevant-tech/analytics-android")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
